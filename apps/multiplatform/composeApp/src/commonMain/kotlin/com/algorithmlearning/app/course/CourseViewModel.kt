package com.algorithmlearning.app.course
import com.algorithmlearning.shared.course.*
import com.algorithmlearning.shared.library.ApiFailure
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
sealed interface CourseState { data object Loading:CourseState; data class List(val page:CoursePage,val query:CourseQuery):CourseState; data class Detail(val lesson:CourseLesson):CourseState; data object Failed:CourseState }
class CourseViewModel(private val courses:CourseRepository,private val scope:CoroutineScope){private val _state=MutableStateFlow<CourseState>(CourseState.Loading);val state:StateFlow<CourseState> = _state.asStateFlow(); private var query=CourseQuery()
 fun load(next:CourseQuery=query){query=next;_state.value=CourseState.Loading;scope.launch {try{_state.value=CourseState.List(courses.list(query),query)}catch(_:ApiFailure){_state.value=CourseState.Failed}}}
 fun category(slug:String?){load(query.copy(category=slug,page=1))}; fun tag(tag:String?){load(query.copy(tag=tag,page=1))}; fun keyword(keyword:String){query=query.copy(keyword=keyword);load(query.copy(page=1))}; fun open(id:String){_state.value=CourseState.Loading;scope.launch {try{_state.value=CourseState.Detail(courses.detail(id))}catch(_:ApiFailure){_state.value=CourseState.Failed}}}; fun back()=load()
}
