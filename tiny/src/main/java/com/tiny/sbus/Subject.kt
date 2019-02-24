package com.tiny.sbus

data class Subject(var key: String,
                      var group: Int,
                      var action: IAction<*>)