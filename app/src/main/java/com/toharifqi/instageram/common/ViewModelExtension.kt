package com.toharifqi.instageram.common

import androidx.lifecycle.ViewModel

inline fun <reified T : ViewModel> factory(noinline creator: () -> T) = ViewModelFactory(creator)
