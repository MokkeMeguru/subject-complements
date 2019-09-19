export interface IProps{
    todos: string[];
    onClickAddButton: (todo: string) => void;
}

export interface IState {
    text: string;
}
