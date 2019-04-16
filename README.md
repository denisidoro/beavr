# beavr

[![npm version](https://badge.fury.io/js/beavr.svg)](https://badge.fury.io/js/beavr)

<img src="https://user-images.githubusercontent.com/3226564/56245772-95f85c80-6076-11e9-9f86-054231221e1f.png" align="right" />

A command-line autocompleter with steroids.

Based on the target command documentation, it suggests arguments, flags and allows you to select between options with [fzf](https://github.com/junegunn/fzf), a fuzzy-finder.

![Demo](https://user-images.githubusercontent.com/3226564/56243794-d6091080-6071-11e9-8940-9b4c79e66a4e.gif)


## Installation

```terminal
npm install beavr
```


## Usage

It's planned for beavr to infer documentation from the target tool's `--help` content. 

In the meantime, you must define a `<your-cmd>.sh` in `$HOME/.config/beavr/` such as [these examples](https://github.com/denisidoro/beavr/tree/master/docs). 

As of now, they must conform to [Neodoc](https://github.com/felixSchl/neodoc)/[docopt](http://docopt.org/) specifications.


## ZSH widget

Simply source [this file](https://github.com/denisidoro/beavr/blob/master/zsh/widget.zsh) in your `.zshrc`.

By default, the widget is trigged by `Alt + G`.


## Widgets for other shells

While there is no widget for other shells, please run the following:
```terminal
beavr <your-cmd> | pbcopy # or similar clipboard tool
```


## Roadmap

Please refer to [this dashboard](https://github.com/denisidoro/beavr/projects/1).


## Etymology

Command-line call builder > builder > [beaver](https://en.wikipedia.org/wiki/Beaver) > beavr


## Icon

Icon made by [Freepik](https://www.freepik.com) from [flaticon](https://www.flaticon.com) is licensed by [CC 3.0 BY](http://creativecommons.org/licenses/by/3.0/).