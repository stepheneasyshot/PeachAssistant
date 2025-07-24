package com.stephen.aiassistant.helper

import com.stephen.aiassistant.platform.permissions.CAMERA
import com.stephen.aiassistant.platform.permissions.GALLERY
import com.stephen.aiassistant.platform.permissions.RECORD_AUDIO
import com.stephen.aiassistant.platform.permissions.STORAGE
import com.stephen.aiassistant.platform.permissions.WRITE_STORAGE
import com.stephen.permissions.DeniedAlwaysException
import com.stephen.permissions.DeniedException
import com.stephen.permissions.Permission
import com.stephen.permissions.PermissionState
import com.stephen.permissions.PermissionsController
import com.stephen.permissions.RequestCanceledException
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PermissionHelper {

    companion object {
        const val TAG = "PermissionHelper"
    }

    private val permissionScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Napier.e("excepion occured: ${throwable.message}", tag = TAG)
        }

    private val _galleryPermissionStateFlow = MutableStateFlow(PermissionState.NotDetermined)
    val galleryPermissionState = _galleryPermissionStateFlow.asStateFlow()

    private val _microphonePermissionStateFlow = MutableStateFlow(PermissionState.NotDetermined)
    val microphonePermissionState = _microphonePermissionStateFlow.asStateFlow()

    private val _cameraPermissionStateFlow = MutableStateFlow(PermissionState.NotDetermined)
    val cameraPermissionState = _cameraPermissionStateFlow.asStateFlow()

    private val _storagePermissionStateFlow = MutableStateFlow(PermissionState.NotDetermined)
    val storagePermissionState = _storagePermissionStateFlow.asStateFlow()
    private val _writeStoragePermissionStateFlow = MutableStateFlow(PermissionState.NotDetermined)
    val writeStoragePermissionState = _writeStoragePermissionStateFlow.asStateFlow()

    private lateinit var permissionsController: PermissionsController

    /**
     * 初始化权限管理器 PermissionsController
     */
    fun setPermissionController(controller: PermissionsController) {
        Napier.i("setPermissionController")
        this.permissionsController = controller
        permissionScope.launch {
            _galleryPermissionStateFlow.update {
                permissionsController.getPermissionState(Permission.GALLERY)
            }
            _microphonePermissionStateFlow.update {
                permissionsController.getPermissionState(Permission.RECORD_AUDIO)
            }
            _cameraPermissionStateFlow.update {
                permissionsController.getPermissionState(Permission.CAMERA)
            }
            _storagePermissionStateFlow.update {
                permissionsController.getPermissionState(Permission.STORAGE)
            }
            _writeStoragePermissionStateFlow.update {
                permissionsController.getPermissionState(Permission.WRITE_STORAGE)
            }
        }
    }

    /**
     * 打开系统权限设置页面
     */
    fun openPermissionSettings() {
        Napier.i("openPermissionSettings")
        permissionsController.openAppSettings()
    }

    /**
     * 相册浏览权限
     */
    fun provideOrRequestGalleryPermission() {
        permissionScope.launch {
            try {
                permissionsController.providePermission(Permission.GALLERY)
                _galleryPermissionStateFlow.update {
                    PermissionState.Granted
                }
            } catch (e: DeniedAlwaysException) {
                _galleryPermissionStateFlow.update {
                    PermissionState.DeniedAlways
                }
            } catch (e: DeniedException) {
                _galleryPermissionStateFlow.update {
                    PermissionState.Denied
                }
            } catch (e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 录音权限
     */
    fun provideOrRequestMicrophonePermission() {
        permissionScope.launch {
            try {
                permissionsController.providePermission(Permission.RECORD_AUDIO)
                _microphonePermissionStateFlow.update {
                    PermissionState.Granted
                }
            } catch (e: DeniedAlwaysException) {
                _microphonePermissionStateFlow.update {
                    PermissionState.DeniedAlways
                }
            } catch (e: DeniedException) {
                _microphonePermissionStateFlow.update {
                    PermissionState.Denied
                }
            } catch (e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 相机权限
     */
    fun provideOrRequestCameraPermission() {
        permissionScope.launch {
            try {
                permissionsController.providePermission(Permission.CAMERA)
                _cameraPermissionStateFlow.update {
                    PermissionState.Granted
                }
            } catch (e: DeniedAlwaysException) {
                _cameraPermissionStateFlow.update {
                    PermissionState.DeniedAlways
                }
            } catch (e: DeniedException) {
                _cameraPermissionStateFlow.update {
                    PermissionState.Denied
                }
            } catch (e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 存储权限
     */
    fun provideOrRequestStoragePermission() {
        permissionScope.launch {
            try {
                permissionsController.providePermission(Permission.STORAGE)
                _storagePermissionStateFlow.update {
                    PermissionState.Granted
                }
            } catch (e: DeniedAlwaysException) {
                _storagePermissionStateFlow.update {
                    PermissionState.DeniedAlways
                }
            } catch (e: DeniedException) {
                _storagePermissionStateFlow.update {
                    PermissionState.Denied
                }
            } catch (e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 写入存储权限
     */
    fun provideOrRequestWriteStoragePermission() {
        permissionScope.launch {
            try {
                permissionsController.providePermission(Permission.WRITE_STORAGE)
                _writeStoragePermissionStateFlow.update {
                    PermissionState.Granted
                }
            } catch (e: DeniedAlwaysException) {
                _writeStoragePermissionStateFlow.update {
                    PermissionState.DeniedAlways
                }
            } catch (e: DeniedException) {
                _writeStoragePermissionStateFlow.update {
                    PermissionState.Denied
                }
            } catch (e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }
}