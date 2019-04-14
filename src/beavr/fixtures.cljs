(ns beavr.fixtures )

(def docstring
"Naval Fate.

Usage:
  naval_fate ship new <name>...
  naval_fate ship <name> move <x> <y> [--speed=<kn>]
  naval_fate ship shoot <x> <y>
  naval_fate ship (set|remove) <x> <y> [--moored|--drifting]
  naval_fate mine (set|remove) <x> <y> [--moored|--drifting]

Options:
  -h --help     Show this screen.
  --version     Show version.
  --speed=<kn>  Speed in knots [default: 10].
  --moored      Moored (anchored) mine.
  --drifting    Drifting mine.")

