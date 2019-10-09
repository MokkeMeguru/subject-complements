from pathlib import Path
import csv
import json
import pandas as pd
import numpy as np
from scipy import stats


def get_all_evaluated_file(root_path):
    root = Path(root_path)
    assert root.is_dir(), 'the path is not a directory'
    return root.glob('**/*.eval.json')


def parse_example(example: Path):
    with example.open() as r:
        json_data = json.load(r)
        task1 = []
        task2_1 = []
        task2_2 = []
        for i in json_data.items():
            idx = int(i[0])
            for j in i[1].items():
                if j[0] == 'task1':
                    task1.append([example.name, idx, j[1]])
                if j[0] == 'task2-1':
                    task2_1.append([example.name, idx, j[1]])
                if j[0] == 'task2-2':
                    task2_2.append([example.name, idx, j[1]])
        task1.sort(key=lambda e: e[1])
        task2_1.sort(key=lambda e: e[1])
        task2_2.sort(key=lambda e: e[1])
        assert len(task2_1) == len(task2_2)
        return task1, task2_1, task2_2


def export_as_tsv(header, task, data):
    export_path = Path('./resources/' + task + '.tsv')
    df = pd.DataFrame(data=data, columns=header)
    with export_path.open(mode='w') as f:
        df.to_csv(f,
                  index=None,
                  encoding='utf-8',
                  sep='\t',
                  quoting=csv.QUOTE_NONNUMERIC)


def get_title(example):
    with example.parent.joinpath(example.stem[:-5]).open() as r:
        json_data = json.load(r)
    return json_data['article_info']['article_title']


def preprocess_result():
    header = ['path', 'sentence_id', 'score']
    root_path = './evaluation-app/resources/datasets'
    line_per_file = []
    task_1 = []
    task_2_1 = []
    task_2_2 = []
    for example in get_all_evaluated_file(root_path):
        t1, t21, t22 = parse_example(example)
        title = get_title(example)
        line_per_file += [[title, example.name, len(t1)]]
        task_1 += t1
        task_2_1 += t21
        task_2_2 += t22
    export_as_tsv(header, 'task_1', task_1)
    export_as_tsv(header, 'task_2_1', task_2_1)
    export_as_tsv(header, 'task_2_2', task_2_2)
    export_as_tsv(['title', 'path', 'sentence_num'], 'basic_info',
                  line_per_file)


def read_basic_info(path: Path):
    print('read basic_info')
    with path.open() as r:
        df = pd.read_table(r)
        maxe = np.max(df['sentence_num'])
        mine = np.min(df['sentence_num'])
        mean = np.mean(df['sentence_num'])
        std = np.std(df['sentence_num'])
        print('|最大| {} |'.format(maxe))
        print('|最小| {} |'.format(mine))
        print('|平均| {} |'.format(mean))
        print('|分散 |{:.6}|'.format(std))


def read_task_1(df):
    print('read task1')
    print('|分割できている|{}|{:.6}|'.format(np.sum(df['t1_score_1']),
                                      np.sum(df['t1_score_1']) / df.shape[0]))
    print('|分割できていない|{}|{:.6}|'.format(np.sum(df['t1_score_-1']),
                                       np.sum(df['t1_score_-1']) /
                                       df.shape[0]))
    print('|判断できない|{}|{:.6}|'.format(np.sum(df['t1_score_0']),
                                     np.sum(df['t1_score_0']) / df.shape[0]))


def read_task_2_1(df, sentence_num):
    print('read task2_1')
    if 't2_1_score_1' in df:
        print('|補完できている|{}|{:.6}|{:.6}|'.format(
            np.sum(df['t2_1_score_1']),
            np.sum(df['t2_1_score_1']) / df.shape[0],
            np.sum(df['t2_1_score_1']) / sentence_num))
    if 't2_1_score_-1' in df:
        print('|補完できていない|{}|{:.6}|{:.6}|'.format(
            np.sum(df['t2_1_score_-1']),
            np.sum(df['t2_1_score_-1']) / df.shape[0],
            np.sum(df['t2_1_score_-1']) / sentence_num))
    if 't2_1_score_0' in df:
        print('|判断できない|{}|{:.6}|{:.6}|'.format(
            np.sum(df['t2_1_score_0']),
            np.sum(df['t_2_1_score_0']) / df.shape[0],
            np.sum(df['t2_1_score_0']) / sentence_num))


def read_task_2_2(df, sentence_num):
    print('read task2_2')
    if 't2_2_score_1' in df:
        print('|自然な文を保っている|{}|{:.6}|{:.6}|'.format(
            np.sum(df['t2_2_score_1']),
            np.sum(df['t2_2_score_1']) / df.shape[0],
            np.sum(df['t2_2_score_1']) / sentence_num))
    if 't2_2_score_-1' in df:
        print('|自然な文を保っていない|{}|{:.6}|{:.6}|'.format(
            np.sum(df['t2_2_score_-1']),
            np.sum(df['t2_2_score_-1']) / df.shape[0],
            np.sum(df['t2_2_score_-1']) / sentence_num))
    if 't2_2_score_0' in df:
        print('|判断できない|{}|{:.6}|{:.6}|'.format(
            np.sum(df['t2_2_score_0']),
            np.sum(df['t2_2_score_0']) / df.shape[0],
            np.sum(df['t2_2_score_0']) / sentence_num))


def main():
    # preprocess_result()
    basic_info = Path('./resources/basic_info.tsv')
    task_1 = Path('./resources/task_1.tsv')
    task_2_1 = Path('./resources/task_2_1.tsv')
    task_2_2 = Path('./resources/task_2_2.tsv')
    read_basic_info(basic_info)
    with task_1.open() as r:
        df = pd.read_table(r)
    df = pd.concat([df, pd.get_dummies(df["score"], prefix='t1_score')],
                   axis=1)
    df = df.drop('score', axis=1)

    sentence_num = df.shape[0]
    read_task_1(df)
    df.to_csv('resultview1.tsv', sep='\t', index=False)

    with task_2_1.open() as r:
        tdf = pd.read_table(r)
    tdf = pd.concat(
        [tdf, pd.get_dummies(tdf["score"], prefix='t2_1_score')], axis=1)
    tdf = tdf.drop('score', axis=1)
    df = pd.merge(df, tdf, on=['path', 'sentence_id'], how='right')

    with task_2_2.open() as r:
        tdf = pd.read_table(r)
    tdf = pd.concat(
        [tdf, pd.get_dummies(tdf["score"], prefix='t2_2_score')], axis=1)
    tdf = tdf.drop('score', axis=1)
    df = pd.merge(df, tdf, on=['path', 'sentence_id'], how='right')

    print('主語を補完する必要があると判断された文: {}'.format(df.shape[0]))
    read_task_2_1(df, sentence_num)
    read_task_2_2(df, sentence_num)

    df.to_csv('resultview2.tsv', sep='\t', index=False)
    t1_minus = (df['t1_score_-1'] == 1)
    t1_equal = (df['t1_score_0'] == 1)
    t1_plus = (df['t1_score_1'] == 1)
    t2_1_minus = (df['t2_1_score_-1'] == 1)
    t2_1_plus = (df['t2_1_score_1'] == 1)
    t2_2_minus = (df['t2_2_score_-1'] == 1)
    t2_2_plus = (df['t2_2_score_1'] == 1)
    from prettytable import PrettyTable

    result = PrettyTable()
    result.field_names = ['', 't2_1 -1', 't2_1 +1', 'sum']
    result.add_row([
        't1 -1', (t1_minus & t2_1_minus).sum(), (t1_minus & t2_1_plus).sum(),
        t1_minus.sum()
    ])
    result.add_row([
        't1 0', (t1_equal & t2_1_minus).sum(), (t1_equal & t2_1_plus).sum(),
        t1_equal.sum()
    ])
    result.add_row([
        't1 +1', (t1_plus & t2_1_minus).sum(), (t1_plus & t2_1_plus).sum(),
        t1_plus.sum()
    ])
    result.add_row([
        'sum',
        t2_1_minus.sum(),
        t2_1_plus.sum(),
        t2_1_minus.sum() + t2_1_plus.sum()
    ])
    print(result.get_string())

    result = PrettyTable()
    result.field_names = ['', 't2_2 -1', 't2_2 +1', 'sum']
    result.add_row([
        't1 -1', (t1_minus & t2_2_minus).sum(), (t1_minus & t2_2_plus).sum(),
        t1_minus.sum()
    ])
    result.add_row([
        't1 0', (t1_equal & t2_2_minus).sum(), (t1_equal & t2_2_plus).sum(),
        t1_equal.sum()
    ])
    result.add_row([
        't1 +1', (t1_plus & t2_2_minus).sum(), (t1_plus & t2_2_plus).sum(),
        t1_plus.sum()
    ])
    result.add_row([
        'sum',
        t2_2_minus.sum(),
        t2_2_plus.sum(),
        t2_2_minus.sum() + t2_2_plus.sum()
    ])
    print(result.get_string())

    result = PrettyTable()
    result.field_names = ['', 't2_1 -1', 't2_1 +1', 'sum']
    result.add_row([
        't2_2 -1', (t2_2_minus & t2_1_minus).sum(), (t2_2_minus & t2_1_plus).sum(),
        t2_2_minus.sum()
    ])
    result.add_row([
        't2_2 1', (t2_2_plus & t2_1_minus).sum(), (t2_2_plus & t2_1_plus).sum(),
        t2_2_plus.sum()
    ])
    
    result.add_row([
        'sum',
        t2_1_minus.sum(),
        t2_1_plus.sum(),
        t2_1_minus.sum() + t2_1_plus.sum()
    ])
    print(result.get_string())


    t1_vs_t2_1 = np.array([
        [(t1_minus & t2_1_minus).sum(), (t1_minus & t2_1_plus).sum()],
        [(t1_equal & t2_1_minus).sum(), (t1_equal & t2_1_plus).sum()],
        [(t1_plus & t2_1_minus).sum(),(t1_plus & t2_1_plus).sum()]])
    result = stats.chi2_contingency(t1_vs_t2_1)
    print('t1_vs_t2_1\nchi2 result {}\np-value {}'.format(
        result[0], result[1]))

    t1_vs_t2_2 = np.array([
        [(t1_minus & t2_2_minus).sum(), (t1_minus & t2_2_plus).sum()],
        [(t1_equal & t2_2_minus).sum(), (t1_equal & t2_2_plus).sum()],
        [(t1_plus & t2_2_minus).sum(),  (t1_plus & t2_2_plus).sum()]])
    result = stats.chi2_contingency(t1_vs_t2_2)
    print('t1_vs_t2_2\nchi2 result {}\np-value {}'.format(
        result[0], result[1]))

    t2_1_vs_t2_2 = np.array([
        [(t2_1_minus & t2_2_minus).sum(), (t2_1_minus & t2_2_plus).sum()],
        [(t2_1_plus & t2_2_minus).sum(), (t2_1_plus & t2_2_plus).sum()]
        ])
    result = stats.chi2_contingency(t2_1_vs_t2_2)
    print('t1_vs_t2_1\nchi2 result {}\np-value {}'.format(
        result[0], result[1]))
