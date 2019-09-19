from __future__ import division, absolute_import
from __future__ import print_function, unicode_literals
from functools import reduce
from visualize_nlp.py_snlp import parse_snlp
import MeCab

import json
import glob
import stanfordnlp
import warnings
warnings.filterwarnings('ignore')

Pipeline = stanfordnlp.Pipeline(lang='ja')

DEFAULT_DIRECTORY = './resources/parsed-sample'
TAGGER = MeCab.Tagger('-Ochasen')
TAGGER.parse('')


def print_infos(infolist: list):
    for info in infolist:
        print(info)


def getjsonlists(directory: str):
    jsonlist = [jsonfile for jsonfile in glob.glob(directory + "/*.json")]
    return jsonlist


def extract_abstructions(jsonfile: str):
    json_data = []
    with open(jsonfile, 'r', encoding='utf-8') as f:
        json_data = json.load(f)
    if len(json_data) > 1:
        return filter(
            lambda x: x['meta'] == 'meta-abstruct' or '概要' in x['name'],
            json_data)
    else:
        return []


def node_to_list(node: MeCab.Node):
    res = []
    while node:
        res.append([node.surface, node.feature])
        node = node.next
    return res


def flatten(x):
    return [
        z for y in x for z in (flatten(y) if hasattr(y, '__iter__')
                               and not isinstance(y, str) else (y, ))
    ]


def preprocess_an_example(example):
    example = list(example)
    if len(example) == 0:
        return []
    contents = []
    for e in example:
        r_content = e['content']
        for content in r_content:
            if len(content) != 0 and content[0] not in ['html', 'list']:
                contents += content
    f_contents = flatten(contents)
    if f_contents != []:
        return filter(lambda x: x not in ['blockquote', 'p', ''], f_contents)
    else:
        return []


def has_dependency_relation(sentence_doc, key_relations=['nsubj']):
    return list(
        map(
            lambda x: x,
            filter(lambda x: x['dependency_relation'] in key_relations,
                   sentence_doc)))


def decode_raw_sentence(sentence_doc):
    return ''.join(
        list(
            map(lambda sentence: sentence['raw_word'],
                sorted(sentence_doc, key=lambda x: x['index']))))


def get_title(docs):
    for element in docs:
        if element['meta'] == 'meta-abstruct':
            return element['info']


def extract_abstruct_sentences(example):
    idx = 0
    res = []
    for x in list(preprocess_an_example(example)):
        rres = []
        for xx in parse_snlp.refinement_doc(parse_snlp.get_doc(Pipeline,
                                                               x)).sentences:
            idx += 1
            # print(decode_raw_sentence(xx))
            rres.append({
                'id': idx,
                'sentence': xx,
                'raw_sentence': decode_raw_sentence(xx)
            })
        res.append(rres)
    return res


def complete_subjects(path):
    example = extract_abstructions(path)
    info = get_title(example)
    abstruct_sentences = extract_abstruct_sentences(example)

    if len(abstruct_sentences) == 0:
        return {
            'path': path,
            'article_info': info,
            'sentences': [],
            'raw_sentences': [],
            'lack_subject_sentences': [],
            'complement_subject_sentences': []
        }

    sentences = []
    sentences = list(
        map(lambda x: {
            'id': x['id'],
            'sentence': x['sentence']
        }, reduce(lambda a, b: a + b, abstruct_sentences)))

    raw_sentences = list(
        map(lambda x: {
            'id': x['id'],
            'sentence': x['raw_sentence']
        }, reduce(lambda a, b: a + b, abstruct_sentences)))
    tmp_lack_subject_sentences = list(
        filter(
            lambda xl: len(xl) != 0,
            map(
                lambda xl: list(
                    filter(
                        lambda x: [] == has_dependency_relation(
                            x['sentence'], key_relations=['nsubj']), xl)),
                abstruct_sentences)))

    if len(tmp_lack_subject_sentences) == 0:
        return {
            'path': path,
            'article_info': info,
            'sentences': sentences,
            'raw_sentences': raw_sentences,
            'lack_subject_sentences': [],
            'complement_subject_sentences': []
        }

    lack_subject_sentences = list(
        map(lambda x: {
            'id': x['id'],
            'sentence': x['raw_sentence']
        }, reduce(lambda a, b: a + b, tmp_lack_subject_sentences)))

    complement_subject_sentences = list(
        map(
            lambda x: {
                'id': x['id'],
                'sentence': info['article_title'] + 'は、' + x['sentence']
            }, lack_subject_sentences))
    return {
        'path': path,
        'article_info': info,
        'sentences': sentences,
        'raw_sentences': raw_sentences,
        'lack_subject_sentences': lack_subject_sentences,
        'complement_subject_sentences': complement_subject_sentences
    }


def main():
    jsonlists = getjsonlists(DEFAULT_DIRECTORY)
    for path in jsonlists:
        complement_results = complete_subjects(path)
        with open('resources/complement-subject/' + path.split('/')[-1],
                  'w',
                  encoding='utf-8') as w:
            json.dump(complement_results, w, indent=4)
    return None


if __name__ == '__main__':
    main()
