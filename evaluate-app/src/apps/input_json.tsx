import React, { useState } from 'react';
import * as ReactDom from 'react-dom';
import { any } from 'prop-types';

interface JSONInfo {
    article_info: { [key: string]: string; };
    complement_subject_sentences: [{ [key: string]: string; }];
    lack_subject_sentences: [{ [key: string]: string; }];
    path: { [key: string]: string; };
    raw_sentences: [{ [key: string]: string; }];
    sentences: [{ [key: string]: string; }];
}

interface Props { }
interface State {
    json_src: string;
    json_body: any;
    json_evaluate: any;
   readed: boolean;
    json_info: JSONInfo;
}


class InputJson extends React.Component<Props, State>{
    constructor(props: Props) {
        super(props);
        this.state = {
            readed: false,
            json_src: 'no file selected',
            json_body: {},
            json_evaluate: {},
            json_info: {
                article_info: {},
                complement_subject_sentences: [{}],
                lack_subject_sentences: [{}],
                path: {},
                raw_sentences: [{}],
                sentences: [{}],
            }
        };
        this.handleChangeFile = this.handleChangeFile.bind(this);
    }
    handleChangeFile(e: any) {
        var files = e.target.files;
        var json_url = window.URL.createObjectURL(files[0]);
        this.setState({ json_src: json_url })
        var reader = new FileReader();
        reader.readAsText(files[0]);
        reader.onload = (e: any) => {
            var obj = JSON.parse(e.target.result)
            this.setState({ json_info: obj });
            this.setState({ readed: true});
        }
    }
    clickPostBtn() {
    }
    render() {
        return (
            <div>
                <div>
                    <input type="file" ref="file" onChange={this.handleChangeFile} />
                </div>
                <div className={'json_infos'}>
                    <p> 記事情報 </p>
                    <table>
                        <tr>
                            <th>記事名</th>
                            <th>{this.state.json_info.article_info['article_title']} / {this.state.json_info.article_info['article_title_yomi']}</th>
                        </tr>
                        <tr>
                            <th>カテゴリ</th> <th>{this.state.json_info.article_info['article_category']}</th>
                        </tr>
                        <tr>
                            <th>更新日時</th> <th>{this.state.json_info.article_info['updated-date']}</th>
                        </tr>
                        <tr>
                            <th>文の数</th> <th>{this.state.readed ? (this.state.json_info.raw_sentences.length) : 0}</th>
                        </tr>
                        <tr>
                            <th>file path</th> <th> {this.state.json_src}</th>
                        </tr>
                    </table>
                </div>
                <div className="abstruction">
                    <p>概要文全体</p>
                    <ul>
                        {this.state.json_info.raw_sentences.map((obj) => {return <li>{obj['sentence']} </li>})}
                    </ul>
                </div>
                <div className="lack subject abstruction">
                    <p>主語が欠けていると判断された文</p>
                    <ul>
                        {this.state.json_info.lack_subject_sentences.map((obj) => {return <li>{obj['sentence']} </li>})}
                    </ul>
                </div>
                <div className="complete subject abstruction">
                    <p>主語を補完した文</p>
                    <ul>
                        {this.state.json_info.complement_subject_sentences.map((obj) => {return <li>{obj['sentence']} </li>})}
                    </ul>
                </div>
            </div>
        )
    }
}

export default InputJson;
