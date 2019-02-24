package com.tiny.sbus

open interface IAction<T> {
   open fun onCall(o: T)
}