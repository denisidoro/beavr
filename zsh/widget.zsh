function _beavr_suggestion {
    zle -U "$(beavr "$CUTBUFFER")"
    zle accept-line
}

zle -N _beavr_suggestion

bindkey '\eg' _beavr_suggestion
