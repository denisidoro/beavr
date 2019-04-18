# beavr


[![npm](https://badge.fury.io/js/beavr.svg)](https://badge.fury.io/js/beavr)

[![CircleCI](https://circleci.com/gh/denisidoro/beavr.svg?style=svg)](https://circleci.com/gh/denisidoro/beavr)

<img src="https://user-images.githubusercontent.com/3226564/56245772-95f85c80-6076-11e9-9f86-054231221e1f.png" align="right" />

A command-line autocompleter with steroids. :muscle:

Based on the desired command spec, beavr suggests arguments and flags. Suggestions can be selected with [fzf](https://github.com/junegunn/fzf), a fuzzy-finder.

![Demo](https://user-images.githubusercontent.com/3226564/56243794-d6091080-6071-11e9-8940-9b4c79e66a4e.gif)

The main objectives are:
- to increase discoverability, by showing all possible actions you can take;
- to prevent you from running auxiliar commands, copying the result into the clipboard and then pasting into the original command;
- to improve terminal usage as a whole.

Sure, you can find autocompleters out there for all your favorite commands.

However, they are very specific and each one may offer a different learning curve.

beavr, on the other hand, intends to be a general purpose platform for speccing any command with a couple of lines. 


## Installation

```bash
brew install fzf # or equivalent
npm install -g beavr
```


## Usage

It's planned for beavr to ship with some command specs and, in case the command isn't available, beaver should infer the spec given the command's `--help` content. 

In the meantime, you must define a `<your-cmd>.sh` in `$HOME/.config/beavr/` such as in [these examples](https://github.com/denisidoro/beavr/tree/master/specs):

```bash
beavr::help() {
  echo "kubectl controls the Kubernetes cluster manager
Usage:
  kubectl get <resource>
  kubectl describe <resource> <id>"
}

beavr::suggestion() {
  case "$1" in 
    "resource") echo "pods nodes deployments" | tr ' ' '\n';;
    "id") kubectl get $resource | tail -n +2;;
  esac
}
```

As of now, they must conform to [neodoc](https://github.com/felixSchl/neodoc)/[docopt](http://docopt.org/) specifications.


## ZSH widget

Simply source [this file](https://github.com/denisidoro/beavr/blob/master/shell/widget.zsh) in your `.zshrc`.

By default, the widget is trigged by `Alt + G`.


## Widgets for other shells

While there is no widget for other shells, please run:
```bash
beavr <your-cmd> | pbcopy # or similar clipboard tool
# paste into the command line
```


## Roadmap

Refer to [this dashboard](https://github.com/denisidoro/beavr/projects/1).

Feel free to add new issues or to upvote existing ones.


## Etymology

Command-line call builder > builder > [beaver](https://en.wikipedia.org/wiki/Beaver) > beavr.


## Icon

Icon made by [Freepik](https://www.freepik.com) from [flaticon](https://www.flaticon.com), licensed by [CC 3.0 BY](http://creativecommons.org/licenses/by/3.0/).
