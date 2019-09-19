import React from 'react'
import { select } from 'd3';

interface Props {
    width: number,
    height: number
};

export default class Circle extends React.Component<Props, {}> {
    ref?: SVGSVGElement;
    componentDidMount(){
        select(this.ref!)
            .append('circle')
            .attr('r', 20)
            .attr('cx', this.props.width / 2)
            .attr('cy', this.props.height / 2)
            .attr('fill', 'red');
    }

    render() {
        const svgstyle= {border: "1px solid black"};
        return (
            <svg className="container"
                 ref={(ref: SVGSVGElement) => this.ref =ref }
                 width={this.props.width}
                 height={this.props.height}
                 style={svgstyle}
            >
            </svg>
        );
    }
}
