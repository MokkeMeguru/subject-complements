import React, { Component } from 'react'
import { select } from 'd3';

interface Props {
    narray: number[],
    id: number,
    width: number
    height: number
}

class BarChart extends Component<Props> {
    ref?: SVGSVGElement;

    constructor(props: Props) {
        super(props);
    }
    componentDidMount() {
        this.drawChart();
    }
    drawChart = () => {
        var data = this.props.narray;
        const svg = select(this.ref!)
        svg.selectAll('rect').data(data).enter().append('rect')
            .attr('x', (d: number, i: number) => i * 60 + 15).attr('y', (d: number, i: number) => this.props.height - d * 10)
            .attr('width', 50).attr('height', (d: number, i: number) => d * 10)
            .attr('fill', 'green');
        svg.selectAll('text').data(data).enter().append('text').text((d: number) => d)
            .attr('x', (d: number, i: number) => i * 60 + 30).attr('y', (d: number, i: number) => this.props.height - (10 * d) - 3);
    }
    render() {
        const svgstyle = { border: "1px solid black" };
        return (
            <svg className='container'
                ref={(ref: SVGSVGElement) => this.ref = ref}
                width={this.props.width}
                height={this.props.height}
                style={svgstyle}
            >
            </svg>
        );
    }
}

export default BarChart;
