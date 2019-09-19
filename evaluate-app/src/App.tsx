import React from 'react';
import logo from './logo.svg';
import './App.css';
import Index from './apps/input_file'
import InputJson from './apps/input_json'

const App: React.FC = () => {
return (
    <div className="App">
        {/* <header className="App-header">
            <img src={logo} className="App-logo" alt="logo" />
            <p>
            Edit <code>src/App.tsx</code> and save to reload.
            </p>
            <a className="App-link" href="https://reactjs.org" target="_blank" rel="noopener noreferrer">
            Learn React
            </a>
            </header>
            <Index />*/}
        <InputJson />
    </div>
);
}

export default App;
