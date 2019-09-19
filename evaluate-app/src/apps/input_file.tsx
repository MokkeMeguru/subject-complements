import React, { useState } from 'react';
import * as ReactDom from 'react-dom';

interface Props { }
interface State { image_src: string; }

class Index extends React.Component<Props, State> {
    constructor(props: Props) {
        super(props);
        this.state  = {
            image_src: ""
        };
        this.handleChangeFile = this.handleChangeFile.bind(this);
    }
    handleChangeFile(e: any) {
        var files = e.target.files;
        var image_url = window.URL.createObjectURL(files[0]);
        this.setState({ image_src: image_url });
    }
    clickPostBtn() {
    }
    render() {
        return (
            <div>
                <input type="file" ref="file" onChange={this.handleChangeFile} />
                <img src={this.state.image_src} style={{ maxHeight: "200px" }} /><br />
                <button onClick={this.clickPostBtn} type="button">投稿する</button>
            </div>
        );
    }
}

export default Index;
