'use strict';
var webpack = require('webpack')
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const path = require('path')

module.exports = (env, argv) => {
  const devMode = argv.mode == 'development';

  let config = {
    entry: {
      index: './src/app/app.ts',
    },

    plugins: [
      new HtmlWebpackPlugin({
        title: 'Development',
        template: './src/public/index.html',
        inject: 'body'
      }),
      new webpack.ProvidePlugin({
        $: 'jquery',
        jQuery: 'jquery'
      })
    ],
    output: {
      filename: './bundle.js',
      path: path.resolve(__dirname, 'dist'),
      clean: true,
    },

    resolve: {
      extensions: [ '.js', '.jsx', '.ts', '.tsx', '.css' ]
    },

    module: {
      rules: [
        {
          test: /\.css$/,
          use: ['style-loader', 'css-loader']
        },
        {
          test: /\.html$/,
          loader: 'raw-loader',
        },
        {
          test: /\.m?js$/,
          exclude: /node_modules/,
          use: {
            loader: 'babel-loader',
            options: {
              presets: ['@babel/preset-env']
            }
          }
        },
        {
          test: /\.ts$/,
          loader: 'ts-loader'
        }
      ]
    },
  };

  if(devMode) {
    config.mode = 'development';
    config.devtool = 'inline-source-map';

    config.devServer = {
      contentBase: './src/public',
      stats: 'minimal',
      port: 8081,
      proxy: {
        '/rest': {
          target: 'http://localhost:8080',
          secure: false
        }
      }
    }
  }

  if(argv.mode == 'production') {
    config.mode = 'production'

    config.plugins.push(new MiniCssExtractPlugin({
      filename: '[name].css',
      chunkFilename: '[id].css',
      ignoreOrder: false,
    }))

    config.optimization = {
      nodeEnv: 'production'
    };
  }

  return config;
}