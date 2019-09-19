const path = require('path');
module.exports = {
    entry:  "./src/app.tsx",
    output: {
        path: `${__dirname}/dist`,
        filename: 'app.js'
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: "ts-loader"
            }
        ]
    },
    resolve: {
        extensions: [".ts", ".js", ".tsx", ".json"]
    },
    devServer : {
        host: '0.0.0.0',
        disableHostCheck: true,
        contentBase: `${__dirname}/dist`,
        port: 3449
    },
};
