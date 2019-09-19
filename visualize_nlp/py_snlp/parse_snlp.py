# Generate Dependency Tree for JSON format
#
# execution abstruction:
# ---------------------------------
# []: Pipeline = stanfordnlp.Pipeline(lang='ja')
# []:  r_doc = refinement_doc(get_doc(Pipeline,  SAMPLE_TEXT))
# []:  r_doc.raw_text
# => '山田さんは、明日の夕食はカレーにしようと言った。その提案に私は賛成した。'
# []:  r_doc.sentences
# => [[
#          {'raw_word': '山田',
#           'index': 1,
#           'lemma': '山田',
#           'upos': 'PROPN',
#           'governor': 2,
#           'dependency_relation': 'compound',
#           'children': []}
#        ... ]
#          ...]
# []: r_doc.tree
# => [[
#          {'raw_word': '言っ',
#           'index': 14,
#           'lemma': '言う',
#           'upos': 'VERB',
#           'governor': 0,
#           'dependency_relation': 'root',
#           'children': [{'raw_word': 'さん',
#                                   'index': 2,
#                                   'lemma': 'さん',
#                                   'upos': 'NOUN',
#                                   'governor': 14,
#                                   'dependency_relation': 'nsubj',
#                                   'children': [...]},
#                                   ...]
#          ...}]
#            ...]
# ---------------------------------
import stanfordnlp
import copy

LEVEL = ["DEBUG", "INFO", "WARN", "ERROR"]
SAMPLE_TEXT = "山田さんは、明日の夕食はカレーにしようと言った。その提案に私は賛成した。"


def future(func):
    def wrapper(*args, **kwargs):
        print("this fucntion will be implemented [{}]".format(func.__name__))
        return func(*args, **kwargs)

    return wrapper


def gen_info(level: int, message: str):
    return "[{}]: {}".format(LEVEL[level], message)


class RefinementDocument:
    def __init__(self, doc: stanfordnlp.pipeline.doc.Document):
        "docstring"
        self.doc = doc
        self.raw_text: str = doc.text
        self.sentences = [
            RefinementDocument._refinement_sentence_by_words(sentence)
            for sentence in self.doc.sentences
        ]
        tmp_sentences = copy.deepcopy(self.sentences)
        self.tree = [
            RefinementDocument.build_dependencies_tree(sentence)
            for sentence in tmp_sentences
        ]

    @classmethod
    def _refinement_sentence_by_words(
            cls, sentence: stanfordnlp.pipeline.doc.Sentence):
        return [
            RefinementDocument._refinement_word(word)
            for word in sentence.words
        ]

    @classmethod
    @future
    def _refinement_sentence_by_tokens(
            cls, sentence: stanfordnlp.pipeline.doc.Sentence):
        return [
            RefinementDocument._refinement_token(token)
            for token in sentence.tokens
        ]

    @classmethod
    def _refinement_word(cls, word: stanfordnlp.pipeline.doc.Word):
        return {
            "raw_word": word.text,
            "index": int(word.index),
            "lemma": word.lemma,
            "upos": word.upos,
            "governor": int(word.governor),
            "dependency_relation": word.dependency_relation
        }

    @classmethod
    def _refinement_token(cls, token: stanfordnlp.pipeline.doc.Token):

        words_info = [
            RefinementDocument._refinement_word(word) for word in token.words
        ]
        governors = set([word_info['governor'] for word_info in words_info])
        return {
            "token": token._text,
            "index": int(token._index),
            "governors": list(governors),
            "words_info": words_info,
        }

    @classmethod
    def build_dependencies_tree(cls, sentence: list):
        roots = []
        rec_roots = []

        def rec_append_children(root, words):
            for word in words:
                if root['index'] == word['governor']:
                    word['children'] = []
                    root['children'].append(rec_append_children(word, words))
            return root

        for word in sentence:
            if word['governor'] == 0:
                word['children'] = []
                roots.append(word)
                sentence.remove(word)

        for root in roots:
            rec_roots.append(rec_append_children(root, sentence))

        return roots


def get_doc(Pipeline: stanfordnlp.Pipeline,
            text: str) -> stanfordnlp.pipeline.doc.Document:
    doc = Pipeline(text)
    return doc


def refinement_doc(doc: stanfordnlp.pipeline.doc.Document
                   ) -> RefinementDocument:
    r_doc = RefinementDocument(doc)
    return r_doc
