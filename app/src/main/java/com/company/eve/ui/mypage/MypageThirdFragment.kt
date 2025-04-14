package com.company.eve.ui.mypage

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.company.eve.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MypageThirdFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecentChargerAdapter
    private lateinit var database: DatabaseReference
    private lateinit var userId: String
    private val recentChargers = mutableListOf<RecentCharger>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mypage_third, container, false)
        recyclerView = view.findViewById(R.id.recent_chargers_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = RecentChargerAdapter(
            recentChargers,
            { charger -> showNavigationConfirmationDialog(charger) },
            { charger -> showDeleteConfirmationDialog(charger) }
        )
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance().reference
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            userId = currentUser.uid
            loadRecentChargers()
        } else {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun loadRecentChargers() {
        val userRef = database.child("UserAccount").child(userId).child("recentClicks")
        userRef.orderByChild("timestamp").limitToLast(30)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    recentChargers.clear()
                    for (data in snapshot.children.reversed()) {
                        val charger = data.getValue(RecentCharger::class.java)
                        if (charger != null) {
                            recentChargers.add(charger)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showNavigationConfirmationDialog(charger: RecentCharger) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("충전소 네비게이션")
        builder.setMessage("${charger.statNm}로 이동하시겠습니까?")
        builder.setPositiveButton("확인") { _, _ ->
            moveChargerToTop(charger)
            navigateToCharger(charger.lat, charger.lng)
        }
        builder.setNegativeButton("취소", null)
        builder.show()
    }

    private fun showDeleteConfirmationDialog(charger: RecentCharger) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("충전소 삭제")
        builder.setMessage("${charger.statNm}를 이용 내역에서 삭제하시겠습니까?")
        builder.setPositiveButton("확인") { _, _ ->
            deleteChargerFromHistory(charger)
        }
        builder.setNegativeButton("취소", null)
        builder.show()
    }

    private fun moveChargerToTop(charger: RecentCharger) {
        val index = recentChargers.indexOfFirst { it.statNm == charger.statNm }
        if (index != -1) {
            recentChargers.removeAt(index)
        }
        recentChargers.add(0, charger)
        adapter.notifyDataSetChanged()
    }

    private fun deleteChargerFromHistory(charger: RecentCharger) {
        val userRef = database.child("UserAccount").child(userId).child("recentClicks")
        userRef.orderByChild("statNm").equalTo(charger.statNm)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        data.ref.removeValue()
                    }
                    recentChargers.remove(charger)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "삭제 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun navigateToCharger(lat: Double, lng: Double) {
        val kakaoMapUri = Uri.parse("kakaomap://route?ep=$lat,$lng&by=CAR")
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
            try {
                kakaoMapIntent.addCategory(Intent.CATEGORY_BROWSABLE)
                startActivity(kakaoMapIntent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "카카오맵을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "카카오맵을 설치해주세요.", Toast.LENGTH_SHORT).show()
            // 카카오맵이 설치되어 있지 않으면 Play 스토어로 이동
            val playStoreUri = Uri.parse("market://details?id=$kakaoMapPackageName")
            val playStoreIntent = Intent(Intent.ACTION_VIEW, playStoreUri)
            try {
                startActivity(playStoreIntent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Play 스토어를 열 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
