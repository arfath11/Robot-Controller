package com.example.robotoperator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robotoperator.domain.usecase.GetAllAnnotationTypesUseCase
import com.example.robotoperator.domain.usecase.GetPointCloudUseCase
import com.example.robotoperator.domain.usecase.SavePointCloudUseCase
import com.example.robotoperator.domain.usecase.DeletePointCloudUseCase
import com.example.robotoperator.domain.usecase.DeleteAllPointCloudsUseCase
import com.example.robotoperator.model.AnnotationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

/**
 * ViewModel for the GLView
 *
 * This ViewModel is responsible for handling the business logic for the 3D view
 * and its interactions, such as selecting points, saving annotations, etc.
 */
@HiltViewModel
class GLViewModel @Inject constructor(
    private val savePointCloudUseCase: SavePointCloudUseCase,
    private val getPointCloudUseCase: GetPointCloudUseCase,
    private val getAllAnnotationTypesUseCase: GetAllAnnotationTypesUseCase,
    private val deletePointCloudUseCase: DeletePointCloudUseCase,
    private val deleteAllPointCloudsUseCase: DeleteAllPointCloudsUseCase
) : ViewModel() {

    private val TAG = "GLViewModel"

    fun generateRandomPointsForTesting(annotationType: AnnotationType, count: Int = 50) {
        viewModelScope.launch {
            Log.d(TAG, "Generating $count random points for ${annotationType.displayName}")
            val points = savePointCloudUseCase.saveRandomData(annotationType, count)
            Log.d(TAG, "Successfully saved ${points.size} random points")
        }
    }


    fun savePointCloud(annotationType: AnnotationType, points: List<FloatArray>) {
        viewModelScope.launch {
            savePointCloudUseCase(annotationType, points)
            Log.d(
                TAG, "From viewmodel Saved ${points.size} points for ${annotationType.displayName}"
            )
        }
    }


    fun deletePointsForType(annotationType: AnnotationType) {
        viewModelScope.launch {
            deletePointCloudUseCase(annotationType)
            Log.d(TAG, "Deleted all points for ${annotationType.displayName}")
        }
    }


    fun deleteAllPoints() {
        viewModelScope.launch {
            deleteAllPointCloudsUseCase()

        }
    }
} 