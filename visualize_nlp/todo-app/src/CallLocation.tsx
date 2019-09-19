// For Learning communicateion with API server ...
import React from 'react';
import './App.css';
 
interface Props {};

interface State {
    zipcode: string,
    address: string
};

export class CallLocation extends React.Component<Props, State> {
    constructor(props: Props) {
        super(props);
        this.state = {
            zipcode: '',
            address: ''
        }
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }
    handleChange = (e: React.FormEvent<HTMLInputElement>) => {
        this.setState({ zipcode: e.currentTarget.value });
    }
    handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        fetch(`https://api.zipaddress.net/?zipcode=${this.state.zipcode}`,
              { mode: 'cors' })
            .then((response: any) => {
                return response.json();
            })
            .then((myJson) => {
                this.setState({ address: myJson.data.fullAddress });
            });
    }
    render() {
        return (
            <div className="CallLocation">
                <form onSubmit={this.handleSubmit}>
                    <p className="App-intro"></p>
                    <input type="text" value={this.state.zipcode} onChange={this.handleChange} />
                    <input type="submit" value="検索" />
                    <p>
                        {this.state.address}
                    </p>
                </form>
            </div>
        );
    }
};
