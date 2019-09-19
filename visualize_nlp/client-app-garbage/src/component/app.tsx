import {IProps, IState} from './types'
import Component from 'react'
import ChangeEvent from 'react'

export default class extends Component<IProps, IState> {
    constructor(props : IProps) {
        super(props);
        this.state = {
            text: ''
        };
    }
    public render() {
        const {todos} = this.props;
        const {text} = this.state;
        return (
            <div style ={{width: '500px', margin: '0 auto'}}>
                <h1>TODO LIST</h1>
                <input type="text" value={text} onChange={this.onTextChange} />
                <button onClick={this.onClickAddButton}>Add Todo</button>
                <ul> {todos.map((todo, i) => (
                    <li key={i}>{todo}</li>))}
                </ul>
            </div>
        );
    }
    private onTextChange = (e: ChangeEvent<HTMLInputElement>) => {
        this.setState({text: e.currentTarget.value});
    };
    private onClickAddButton = () => {
        const { onClickAddButton } = this.props;
        const { text} = this.state;
        onClickAddButton(text);
    };
}
