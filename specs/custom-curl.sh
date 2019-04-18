#!/usr/bin/env bash

beavr::help() {
  echo "Command for calling an arbitrary URL from a service

Usage:
  service-curl [options] <http-method> <prototype> <service> <path>
  
Options:
  -e --env <env>        Infrastructure environment
  -f --format <format>  Content-type format"
}

beavr::suggestion() {
  case "$1" in 
    "http-method") echo "GET POST PUT PATCH" | tr ' ' '\n';;
    "prototype") echo "s0 s1 s2 s3 global" | tr ' ' '\n';;
    "--env") echo "staging prod" | tr ' ' '\n';;
    "--format") echo "xml json yaml edn transit" | tr ' ' '\n';;
    "service") _work services;;
    "path") _work routes "$service" | column -ts ',';;
  esac
}

_work() {
  local readonly domain="$1"
  shift
  case $domain in 
    "services") echo "customer cart warehouse geo finance" | tr ' ' '\n';;
    "routes") echo -e "/api/carts/:id,Get cart for a given ID\n/api/customers/:id/carts,Get cart for a given customer ID\n/api/version,Get service git commit hash";;
  esac
}
