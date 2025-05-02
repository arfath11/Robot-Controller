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
    
    /**
     * Generate and save random points for testing
     * @param annotationType The annotation type to use
     * @param count Number of random points to generate
     */
    fun generateRandomPointsForTesting(annotationType: AnnotationType, count: Int = 50) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Generating $count random points for ${annotationType.displayName}")
                val points = savePointCloudUseCase.saveRandomData(annotationType, count)
                Log.d(TAG, "Successfully saved ${points.size} random points")
            } catch (e: Exception) {
                Log.e(TAG, "Error generating random points: ${e.message}")
            }
        }
    }
    
    /**
     * Save a point cloud to the database
     * @param annotationType The annotation type
     * @param points The 3D points to save
     */
    fun savePointCloud(annotationType: AnnotationType, points: List<FloatArray>) {
        viewModelScope.launch {
            try {
                savePointCloudUseCase(annotationType, points)
                Log.d(TAG, "Saved ${points.size} points for ${annotationType.displayName}")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving points: ${e.message}")
            }
        }
    }
    
    /**
     * Delete points for a specific annotation type
     * @param annotationType The annotation type to delete points for
     */
    fun deletePointsForType(annotationType: AnnotationType) {
        viewModelScope.launch {
            try {
                deletePointCloudUseCase(annotationType)
                Log.d(TAG, "Deleted all points for ${annotationType.displayName}")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting points: ${e.message}")
            }
        }
    }
    
    /**
     * Delete all points in the database across all annotation types
     */
    fun deleteAllPoints() {
        viewModelScope.launch {
            try {
                deleteAllPointCloudsUseCase()
                Log.d(TAG, "Deleted all points from database")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting all points: ${e.message}")
            }
        }
    }
} 