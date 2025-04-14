package com.company.eve.ui.home.kotlintest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import com.company.eve.R
import com.company.eve.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import android.graphics.PorterDuff
import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory

private const val TAG = "HomeFragment"
private const val LOCATION_PERMISSION_REQUEST_CODE = 1

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var database: DatabaseReference
    private lateinit var clusterManager: ClusterManager<Camera>
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String

    private lateinit var searchView: SearchView
    private lateinit var btnFindShortestRoute: Button
    private lateinit var searchResultsListView: ListView
    private lateinit var nearestChargersListView: ListView
    private var isShowingNearestChargers = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 뷰 바인딩
        searchView = binding.searchView
        btnFindShortestRoute = binding.btnFindShortestRoute
        searchResultsListView = binding.searchResultsListView
        nearestChargersListView = binding.nearestChargersListView

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        auth = FirebaseAuth.getInstance()

        userId = auth.currentUser?.uid ?: "temporaryUserId"

        if (userId == "temporaryUserId") {
            Toast.makeText(requireContext(), "사용자가 로그인되지 않았습니다. 임시 사용자 ID를 사용합니다.", Toast.LENGTH_SHORT).show()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        initializeDatabase()

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    findNavController().navigate(R.id.action_homeFragment_to_HomeFragment)
                    true
                }
                R.id.navigation_alarm -> {
                    findNavController().navigate(R.id.action_homeFragment_to_alarmFragment)
                    true
                }
                R.id.navigation_history -> {
                    findNavController().navigate(R.id.action_homeFragment_to_mypageThirdFragment)
                    true
                }
                else -> false
            }
        }

        // 돋보기 아이콘 및 텍스트 색상 설정
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(
            ContextCompat.getColor(requireContext(), android.R.color.black),
            PorterDuff.Mode.SRC_IN
        )

        val textView = searchView.findViewById<AutoCompleteTextView>(androidx.appcompat.R.id.search_src_text)
        textView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        textView.setHintTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))

        // 검색창 포커스 이벤트 처리
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (searchView.query.isNullOrEmpty()) {
                    // 검색창에 포커스가 있고 검색어가 없을 때 가까운 충전소 리스트 표시
                    showNearestChargers()
                    nearestChargersListView.visibility = View.VISIBLE
                    searchResultsListView.visibility = View.GONE
                } else {
                    // 검색창에 포커스가 있고 검색어가 있을 때 자동완성 리스트 표시
                    searchLocations(searchView.query.toString())
                    nearestChargersListView.visibility = View.GONE
                    searchResultsListView.visibility = View.VISIBLE
                }
            } else {
                // 검색창 포커스를 잃었을 때 리스트 숨기기 및 검색어 지우기
                nearestChargersListView.visibility = View.GONE
                searchResultsListView.visibility = View.GONE
                searchView.setQuery("", false) // 검색어 지우기
            }
        }

        // 검색바 입력 처리
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색어 입력 후 리스트 숨기기
                nearestChargersListView.visibility = View.GONE
                searchResultsListView.visibility = View.GONE
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // 검색어가 없을 때 리스트를 숨김
                    nearestChargersListView.visibility = View.GONE
                    searchResultsListView.visibility = View.GONE
                    isShowingNearestChargers = false
                } else {
                    // 검색어가 있을 때 자동완성 리스트 표시
                    searchLocations(newText)
                    nearestChargersListView.visibility = View.GONE
                    searchResultsListView.visibility = View.VISIBLE
                }
                return true
            }
        })

        // 검색 결과 항목 클릭 처리
        searchResultsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedChargerName = searchResultsListView.adapter.getItem(position) as String
            searchChargerByName(selectedChargerName) { selectedCharger ->
                selectedCharger?.let {
                    displayChargerOnMap(it)
                }
            }
            searchResultsListView.visibility = View.GONE
            searchView.clearFocus()
        }

        // 빠른 길 찾기 버튼 클릭 처리
        btnFindShortestRoute.setOnClickListener {
            navigateToNearestPlace()
        }

        // 가까운 충전소 목록 항목 클릭 처리
        nearestChargersListView.setOnItemClickListener { _, _, position, _ ->
            val selectedChargerName = nearestChargersListView.adapter.getItem(position) as String
            searchChargerByName(selectedChargerName) { selectedCharger ->
                selectedCharger?.let {
                    displayChargerOnMap(it)
                }
            }
            nearestChargersListView.visibility = View.GONE
            isShowingNearestChargers = false
            searchView.clearFocus()
        }
    }

    private fun initializeDatabase() {
        val databaseUrl = "https://eve-52292-230d9.firebaseio.com"
        database = FirebaseDatabase.getInstance(databaseUrl).reference.child("stations")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.clear()

        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(37.751853, 128.8760574),
                16f
            )
        )

        mMap.setMinZoomPreference(4f)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        val userLocation = LatLng(it.latitude, it.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16f))
                        btnFindShortestRoute.isEnabled = true
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "위치 가져오기 실패", e)
                }
        }

        setupClusterManager()
        setupMarkerClickListener()
        addMarkersFromDatabase()
        setupMapListeners()
    }

    private fun setupClusterManager() {
        clusterManager = ClusterManager(requireContext(), mMap)
        mMap.setOnCameraIdleListener(clusterManager)
        clusterManager.renderer =
            object : DefaultClusterRenderer<Camera>(requireContext(), mMap, clusterManager) {
                override fun onBeforeClusterItemRendered(item: Camera, markerOptions: MarkerOptions) {
                    val iconBitmap = when (item.limit) {
                        "이용가능" -> BitmapFactory.decodeResource(resources, R.drawable.poi_green)
                        else -> BitmapFactory.decodeResource(resources, R.drawable.poi_pink)
                    }
                    val icon = BitmapDescriptorFactory.fromBitmap(iconBitmap)
                    markerOptions.icon(icon).title(item.statNm)
                    super.onBeforeClusterItemRendered(item, markerOptions)
                }
            }
    }

    private fun setupMarkerClickListener() {
        clusterManager.setOnClusterItemClickListener { camera ->
            val charger = Charger(
                province = camera.province,
                city = camera.city,
                addr = camera.addr,
                lat = camera.lat,
                lng = camera.lng,
                statNm = camera.statNm,
                place = camera.place,
                space = camera.space,
                type = camera.type,
                typeS = camera.typeS,
                bnm = camera.bnm,
                busiNm = camera.busiNm,
                output = camera.output,
                Ctype = camera.Ctype,
                limit = camera.limit,
                chgerId = camera.chgerId
            )
            displayChargerInfo(charger)
            true
        }
    }

    private fun addMarkersFromDatabase() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                clusterManager.clearItems() // 기존 마커 제거
                val visibleRegion = mMap.projection.visibleRegion // 현재 카메라의 가시 영역

                for (chargerSnapshot in dataSnapshot.children) {
                    val charger = chargerSnapshot.getValue(Charger::class.java)
                    charger?.let {
                        val chargerLocation = LatLng(it.lat ?: 0.0, it.lng ?: 0.0)
                        // 마커가 현재 카메라의 가시 영역 내에 있는지 확인
                        if (visibleRegion.latLngBounds.contains(chargerLocation)) {
                            addChargerToCluster(it) // 클러스터에 추가
                        }
                    }
                }
                clusterManager.cluster() // 클러스터링 실행
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Firebase에서 데이터 읽기 실패", databaseError.toException())
            }
        })
    }

    private fun setupMapListeners() {
        mMap.setOnCameraIdleListener {
            addMarkersFromDatabase() // 카메라 이동 후 마커 업데이트
        }
    }

    private fun addChargerToCluster(charger: Charger) {
        val camera = Camera(
            province = charger.province ?: "",
            city = charger.city ?: "",
            addr = charger.addr ?: "",
            lat = charger.lat ?: 0.0,
            lng = charger.lng ?: 0.0,
            statNm = charger.statNm ?: "",
            place = charger.place ?: "",
            space = charger.space ?: "",
            type = charger.type ?: "",
            typeS = charger.typeS ?: "",
            bnm = charger.bnm ?: "",
            busiNm = charger.busiNm ?: "",
            output = charger.output ?: "",
            Ctype = charger.Ctype ?: "",
            limit = charger.limit ?: "",
            chgerId = charger.chgerId ?: 0
        )
        clusterManager.addItem(camera)
    }

    private fun displayChargerInfo(charger: Charger) {
        ChargerInfoBottomSheetFragment.newInstance(
            charger,
            userId
        ).show(childFragmentManager, ChargerInfoBottomSheetFragment.TAG)
    }

    private fun searchLocations(query: String) {
        database.orderByChild("statNm").startAt(query).endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val results = mutableListOf<String>()
                    for (snapshot in dataSnapshot.children) {
                        val chargerName = snapshot.child("statNm").getValue(String::class.java)
                        chargerName?.let { results.add(it) }
                    }
                    if (results.isNotEmpty()) {
                        val adapter = ArrayAdapter(
                            requireContext(),
                            R.layout.list_item,
                            R.id.text_view_item,
                            results
                        )
                        searchResultsListView.adapter = adapter
                        searchResultsListView.visibility = View.VISIBLE
                    } else {
                        searchResultsListView.visibility = View.GONE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "Firebase에서 데이터 읽기 실패", databaseError.toException())
                }
            })
    }

    private fun searchChargerByName(chargerName: String, callback: (Charger?) -> Unit) {
        database.orderByChild("statNm").equalTo(chargerName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val charger = dataSnapshot.children.firstOrNull()?.getValue(Charger::class.java)
                    callback(charger)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "Firebase에서 데이터 읽기 실패", databaseError.toException())
                    callback(null)
                }
            })
    }

    private fun displayChargerOnMap(charger: Charger) {
        val chargerLocation = LatLng(charger.lat ?: 0.0, charger.lng ?: 0.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chargerLocation, 16f))
        displayChargerInfo(charger)
    }

    private fun saveChargerClickToFirebase(charger: Charger) {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("UserAccount")
            .child(userId)
            .child("recentClicks")

        // Firebase에서 중복된 항목 제거
        userRef.orderByChild("statNm").equalTo(charger.statNm).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 중복된 항목 삭제
                for (data in snapshot.children) {
                    data.ref.removeValue()
                }

                // 중복 제거 후 새 데이터 저장
                val recentCharger = mapOf(
                    "statNm" to charger.statNm,
                    "addr" to charger.addr,
                    "lat" to charger.lat,
                    "lng" to charger.lng,
                    "timestamp" to System.currentTimeMillis()
                )

                userRef.push().setValue(recentCharger).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "충전소 기록 저장 성공")
                    } else {
                        Log.e(TAG, "충전소 기록 저장 실패", task.exception)
                        Toast.makeText(requireContext(), "충전소 기록 저장 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "중복 확인 중 Firebase 오류 발생", error.toException())
                Toast.makeText(requireContext(), "기록 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToNearestPlace() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val userLocation = LatLng(it.latitude, it.longitude)
                    val items = clusterManager.algorithm.items.toList()
                    val nearestCharger = items.minByOrNull { camera ->
                        calculateDistance(
                            userLocation.latitude, userLocation.longitude,
                            camera.position.latitude, camera.position.longitude
                        )
                    }

                    nearestCharger?.let { camera ->
                        val charger = Charger(
                            province = camera.province,
                            city = camera.city,
                            addr = camera.addr,
                            lat = camera.lat,
                            lng = camera.lng,
                            statNm = camera.statNm,
                            place = camera.place,
                            space = camera.space,
                            type = camera.type,
                            typeS = camera.typeS,
                            bnm = camera.bnm,
                            busiNm = camera.busiNm,
                            output = camera.output,
                            Ctype = camera.Ctype,
                            limit = camera.limit,
                            chgerId = camera.chgerId
                        )
                        // 이용내역 저장
                        saveChargerClickToFirebase(charger)

                        // 길찾기 실행
                        searchLoadToKakaoMap(charger.statNm ?: "")
                    } ?: run {
                        Toast.makeText(requireContext(), "근처 충전소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "위치 가져오기 실패", e)
                Toast.makeText(requireContext(), "위치를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371e3
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }

    private fun searchLoadToKakaoMap(destination: String) {
        try {
            val kakaoMapUri = Uri.parse("kakaomap://search?q=$destination")
            val kakaoMapIntent = Intent(Intent.ACTION_VIEW, kakaoMapUri)

            // 카카오맵 패키지 이름
            val kakaoMapPackageName = "net.daum.android.map"

            // 패키지 매니저를 통해 카카오맵 설치 여부 확인
            val packageManager = requireContext().packageManager
            val isKakaoMapInstalled = try {
                packageManager.getPackageInfo(kakaoMapPackageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }

            if (isKakaoMapInstalled) {
                // 카카오맵이 설치되어 있으면 앱으로 연결
                startActivity(kakaoMapIntent)
            } else {
                Toast.makeText(requireContext(), "카카오맵을 설치해주세요.", Toast.LENGTH_SHORT).show()
                // 카카오맵이 설치되어 있지 않으면 Play 스토어로 이동
                val playStoreUri = Uri.parse("market://details?id=$kakaoMapPackageName")
                val playStoreIntent = Intent(Intent.ACTION_VIEW, playStoreUri)
                startActivity(playStoreIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "KakaoMap 실행 오류", e)
            Toast.makeText(requireContext(), "카카오맵을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(callback: (Location?) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                callback(location)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "위치 가져오기 실패", e)
                callback(null)
            }
    }

    private fun showNearestChargers() {
        if (!isShowingNearestChargers) {
            getLastKnownLocation { location ->
                location?.let {
                    val userLocation = LatLng(it.latitude, it.longitude)
                    getNearestChargers(userLocation) { nearestChargers ->
                        val adapter = ArrayAdapter(
                            requireContext(),
                            R.layout.list_item,
                            R.id.text_view_item,
                            nearestChargers.map { charger -> charger.statNm ?: "" }
                        )
                        nearestChargersListView.adapter = adapter
                        nearestChargersListView.visibility = View.VISIBLE
                        isShowingNearestChargers = true
                    }
                } ?: run {
                    Toast.makeText(requireContext(), "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getNearestChargers(userLocation: LatLng, callback: (List<Charger>) -> Unit) {
        val allChargers = mutableListOf<Charger>()
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (chargerSnapshot in dataSnapshot.children) {
                    val charger = chargerSnapshot.getValue(Charger::class.java)
                    charger?.let {
                        allChargers.add(it)
                    }
                }
                allChargers.sortBy { charger ->
                    val chargerLocation = LatLng(charger.lat ?: 0.0, charger.lng ?: 0.0)
                    distanceBetween(userLocation, chargerLocation)
                }
                callback(allChargers.take(10)) // 가장 가까운 10개의 충전소를 가져옴
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Firebase에서 데이터 읽기 실패", databaseError.toException())
            }
        })
    }

    private fun distanceBetween(start: LatLng, end: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            results
        )
        return results[0]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
