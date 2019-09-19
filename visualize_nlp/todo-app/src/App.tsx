import React from 'react';
import logo from './logo.svg';
import './App.css';
import { CallLocation } from './CallLocation';
import Circle from './D3Tutorial/Circle';
import BarChart from './D3Tutorial/BarChart'

const App: React.FC = () => {
    return (
        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo" />
                <p>
                    Edit <code>src/App.tsx</code> and save to reload.
                </p>
                <a
                    className="App-link"
                    href="https://reactjs.org"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    Learn React
                </a>
            </header>
            <CallLocation></CallLocation>
            <Circle width={200}
                height={100}></Circle>
            <BarChart narray={[1, 2, 4, 6, 2, 1, 10, 5]} id={1} width={500} height={120}> </BarChart>
        </div>
    );
}

export default App;
