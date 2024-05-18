package com.projecte.mewnagochi

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.projecte.mewnagochi.screens.main.MainScreen
import com.projecte.mewnagochi.screens.profile.InternetPreferenceState
import com.projecte.mewnagochi.screens.profile.InternetPreferenceStateDataStore
import com.projecte.mewnagochi.services.auth.NetworkConnection
import com.projecte.mewnagochi.services.notification.MyFirebaseMessagingService
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import com.projecte.mewnagochi.stats.HealthConnectAvailability
import com.projecte.mewnagochi.stats.HealthConnectManager
import com.projecte.mewnagochi.stats.StatsViewModel
import com.projecte.mewnagochi.ui.theme.MewnagochiTheme

class MainActivity : ComponentActivity() {
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.i("firestoreNetwork", "Connected to $network")
            StorageServiceImpl().setFirestoreNetworkEnabled()
        }

        // Network capabilities have changed for the network
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            Log.i("firestoreNetwork",networkCapabilities.toString())
            if(networkCapabilities.toString().contains("WIFI")){
                StorageServiceImpl().setFirestoreNetworkEnabled()
                NetworkConnection.isAvailable=true
            }
            else{
                StorageServiceImpl().setFirestoreNetworkDisabled()
                NetworkConnection.isAvailable=false
            }
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)

            Log.i("firestoreNetwork", "Disconnected from $network")
        }
    }

    private val networkRequest : NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val mFMS = MyFirebaseMessagingService()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        setContent {
            MewnagochiTheme() {
                val internetPreferenceState by this.InternetPreferenceStateDataStore.data.collectAsState(initial = InternetPreferenceState())
                var isRegistered by remember { mutableStateOf(false)}
                if(internetPreferenceState.internetPreferenceSelected==1){
                    if(isRegistered)connMgr.unregisterNetworkCallback(networkCallback)
                    connMgr.requestNetwork(networkRequest,networkCallback)
                    isRegistered=true
                }
                else{
                    if(isRegistered)connMgr.unregisterNetworkCallback(networkCallback)
                    StorageServiceImpl().setFirestoreNetworkEnabled()
                    NetworkConnection.isAvailable=true
                    isRegistered=false
                }
                val sVM = StatsViewModel()
                val sHS = SnackbarHostState()
                val hcm by lazy {
                    HealthConnectManager(this)
                }
                //initialization of healthPermissionLauncher
                sVM.healthPermissionLauncher =
                    rememberLauncherForActivityResult(contract = hcm.requestPermissionsActivityContract(),
                        onResult = { grantedPermissions: Set<String> ->
                            sVM.onPermissionResult(
                                hcm,
                                this,
                                grantedPermissions,
                                sHS,
                                this
                            )
                        })
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    //MyNavGraph(context = this, activity = this, scope = this.lifecycleScope)
                    MainScreen(context = this, mFMS = mFMS, sVM = sVM, sHS=sHS, hcm=hcm,)
                }
            }
        }
    }
}


