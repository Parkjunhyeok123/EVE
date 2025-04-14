package com.company.eve.ui.home.kotlintest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.company.eve.databinding.FragmentChargerInfoBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.company.eve.R
import com.company.eve.ui.mypage.FavoriteCharger

class ChargerInfoBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentChargerInfoBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var charger: Charger? = null
    private lateinit var database: DatabaseReference
    private lateinit var userId: String
    private var isFavorite = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val TAG = "ChargerInfoBottomSheet"
        const val LOCATION_PERMISSION_REQUEST_CODE = 2

        fun newInstance(charger: Charger, userId: String): ChargerInfoBottomSheetFragment {
            val fragment = ChargerInfoBottomSheetFragment()
            val args = Bundle()
            args.putParcelable("charger", charger)
            args.putString("userId", userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            charger = it.getParcelable("charger")
            userId = it.getString("userId").orEmpty()
        }
        database = FirebaseDatabase.getInstance().reference
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChargerInfoBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        charger?.let {
            binding.statNm.text = it.statNm
            binding.limit.text = it.limit
            binding.addr.text = it.addr
            binding.city.text = it.city
            binding.province.text = it.province
            // 위도와 경도 정보를 표시하지 않도록 주석 처리함
            // binding.location.text = "${it.lat} / ${it.lng}"

            // "이용가능" 여부에 따라 색상 설정
            if (it.limit == "이용가능") {
                binding.limit.setTextColor(resources.getColor(R.color.green, null)) // 초록색으로 설정
            } else {
                binding.limit.setTextColor(resources.getColor(R.color.red, null)) // 빨간색으로 설정
            }

            checkIfFavorite(userId, it.chgerId ?: 0)
        }

        binding.favoriteButton.setOnClickListener {
            charger?.let { charger ->
                if (isFavorite) {
                    removeFavorite(userId, charger.chgerId ?: 0)
                } else {
                    addFavorite(userId, charger)
                }
            }
        }

        binding.navigateButton.setOnClickListener {
            saveChargerClickToFirebase()
            searchLoadToKakaoMap()
        }

        val openDetailActivity = View.OnClickListener {
            val intent = Intent(activity, CameraActivity::class.java).apply {
                putExtra("province", charger?.province)
                putExtra("city", charger?.city)
                putExtra("addr", charger?.addr)
                putExtra("statNm", charger?.statNm)
                putExtra("place", charger?.place)
                putExtra("space", charger?.space)
                putExtra("type", charger?.type)
                putExtra("typeS", charger?.typeS)
                putExtra("bnm", charger?.bnm)
                putExtra("busiNm", charger?.busiNm)
                putExtra("output", charger?.output)
                putExtra("Ctype", charger?.Ctype)
                putExtra("limit", charger?.limit)
                putExtra("chgerId", charger?.chgerId)
                // 위도와 경도를 넘기지 않음
                // putExtra("lat", charger?.lat)
                // putExtra("lng", charger?.lng)
            }
            startActivity(intent)
            dismiss()
        }

        binding.root.setOnClickListener(openDetailActivity)
    }

    private fun searchLoadToKakaoMap() {
        charger?.let { charger ->
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // 위치 권한 요청
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                return
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val kakaoMapUri = Uri.parse("kakaomap://route?sp=${location.latitude},${location.longitude}&ep=${charger.lat},${charger.lng}&by=CAR")
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
                        // 카카오맵이 설치되어 있으면 길찾기 실행
                        try {
                            startActivity(kakaoMapIntent)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "카카오맵을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "카카오맵을 설치해주세요.", Toast.LENGTH_SHORT).show()
                        // 카카오맵이 설치되어 있지 않으면 Play 스토어로 이동
                        val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$kakaoMapPackageName"))
                        try {
                            startActivity(playStoreIntent)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Play 스토어로 이동할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveChargerClickToFirebase() {
        charger?.let { charger ->
            val userRef = database.child("UserAccount").child(userId).child("recentClicks")

            // Firebase에서 중복된 항목 제거
            userRef.orderByChild("statNm").equalTo(charger.statNm).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // 중복된 항목이 있다면 삭제
                    for (data in snapshot.children) {
                        data.ref.removeValue()
                    }

                    // 중복 제거 후 새 데이터 저장
                    val recentCharger = mapOf(
                        "statNm" to charger.statNm,
                        "addr" to charger.addr,
                        "lat" to charger.lat,
                        "lng" to charger.lng,
                        "timestamp" to System.currentTimeMillis() // 최신 데이터 기록
                    )
                    userRef.push().setValue(recentCharger).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("ChargerSave", "충전소 기록이 저장되었습니다.")
                        } else {
                            Log.e("ChargerSave", "충전소 기록 저장에 실패했습니다.")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChargerSave", "기록 저장 중 오류가 발생했습니다: ${error.message}")
                }
            })
        }
    }

    private fun addFavorite(userId: String, charger: Charger) {
        val favorite = FavoriteCharger(
            charger.chgerId,
            charger.statNm,
            charger.addr,
            charger.city,
            charger.limit,
            charger.output,
            charger.statNm,
            charger.typeS,
            charger.lat,
            charger.lng,
            charger.place,
            charger.province,
            charger.space,
            charger.type,
            charger.Ctype,
            charger.bnm,
            charger.busiNm
        )
        val favoriteRef = database.child("UserAccount").child(userId).child("favorites").child(charger.chgerId.toString())
        favoriteRef.setValue(favorite).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                binding.favoriteButton.setImageResource(R.drawable.on_star)
                isFavorite = true
            } else {
                Toast.makeText(context, "즐겨찾기 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeFavorite(userId: String, chargerId: Int) {
        val favoriteRef = database.child("UserAccount").child(userId).child("favorites").child(chargerId.toString())
        favoriteRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "즐겨찾기에서 제거되었습니다.", Toast.LENGTH_SHORT).show()
                binding.favoriteButton.setImageResource(R.drawable.off_star)
                isFavorite = false
            } else {
                Toast.makeText(context, "즐겨찾기 제거에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIfFavorite(userId: String, chargerId: Int) {
        val favoriteRef = database.child("UserAccount").child(userId).child("favorites").child(chargerId.toString())
        favoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding != null) {
                    if (snapshot.exists()) {
                        binding.favoriteButton.setImageResource(R.drawable.on_star)
                        isFavorite = true
                    } else {
                        binding.favoriteButton.setImageResource(R.drawable.off_star)
                        isFavorite = false
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리 필요 시 작성
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
