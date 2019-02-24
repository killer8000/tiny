package com.tiny.sbus

import android.util.SparseArray

object Sbus {
    var mKeys: ArrayList<String> = ArrayList()
    var datas: SparseArray<ArrayList<Subject>> = SparseArray()
    fun <T> subscribe(o: Any, @SEvent.Event group: Int, action: IAction<T>) {
        var key = o.hashCode().toString() + ":" + group
        if (mKeys.contains(key)) {
            var subjects = datas.get(group)
            subjects.forEach {
                if (it.key == key) {
                    it.action = action
                }
            }
        } else {
            var s = Subject(key, group, action)
            mKeys.add(key)// 记录key
            var group_subject = datas.get(group)
            if (null == group_subject) {
                group_subject = ArrayList()
                group_subject.add(s)
                datas.put(group,group_subject)
            } else {
                datas.get(group).add(s)
            }
        }
    }

    fun unsubscribe(o: Any, @SEvent.Event group: Int) {
        var key = o.hashCode().toString() + ":" + group
        if (null != mKeys && null != datas) {
            var iterator = mKeys.iterator()
            while (iterator?.hasNext()) {
                var next = iterator.next()
                if (key == next) {
                    iterator.remove()
                    break
                }
            }
            var subjects = datas.get(group)
            var iterator1 = subjects?.iterator()
            while (iterator1?.hasNext() == true) {
                var next = iterator1?.next()
                if (key == next?.key) {
                    iterator1?.remove()
                    break
                }
            }

        }
    }

    fun <T> publish(@SEvent.Event group: Int, param: T) {
        var subjects = datas.get(group)
        subjects?.forEach {
            (it.action as? IAction<T>)?.onCall(param)
//            it.action.onCall(param)
        }
    }

}