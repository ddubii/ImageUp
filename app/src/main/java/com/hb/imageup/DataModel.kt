package com.hb.imageup

class DataModel {
    data class TodoInfo1(val result_caption_str : TaskInfo)
    data class TodoInfo2(val result_caption_sub : TaskInfo)

//    {"result_caption_str":"일부 덤불 근처의 물에서 걷기","result_caption_sub":"고양이"}

    data class TaskInfo(val task : String)
}