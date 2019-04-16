function _beavr_suggestion {
    zle kill-whole-line
    zle -U "$(node "beavr" "$(echo "$CUTBUFFER" | xargs)")"
    zle accept-line
}

zle -N _beavr_suggestion

bindkey '\eg' _beavr_suggestion
