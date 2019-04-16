#!/usr/bin/env bash
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
source "$DIR/work.sh"

beavr::help() {
  echo "Command for calling an arbitrary URL from a service

Usage:
  curl [options] <http-method> <prototype> <service> <path>
  
Options:
  -e --env <env>        Infrastructure environment
  -f --format <format>  Content-type format"
}

beavr::suggestion() {
  case "$1" in 
    "http-method") echo "GET POST PUT PATCH" | tr ' ' '\n';;
    "prototype") echo "s0 s1 s2 s3 global" | tr ' ' '\n';;
    "env") echo "staging prod" | tr ' ' '\n';;
    "--format") echo "xml json yaml edn transit" | tr ' ' '\n';;
    "service") work services;;
    "path") work routes "$service" | column -ts ',';;
  esac
}
