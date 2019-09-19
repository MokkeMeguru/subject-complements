import * as React from 'react';
import * as _ from "lodash";
import TodoComponent from './component/app'


function component() {
    const element = document.createElement('div');
    element.innerHTML = _.join(['Hello', 'webpack'], ' ');
    return element;
}

class App extends React.Component {
    public render() {
        return (
            <div>
                <TodoComponent todos = {['foo', 'bar']}
                                        onClickAddButton={(todo: string): void => {
                                            console.log(todo);
                                        }}/>
            </div>
        );
    }
}

document.body.appendChild(App.render());
