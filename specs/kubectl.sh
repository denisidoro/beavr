#!/usr/bin/env bash

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
