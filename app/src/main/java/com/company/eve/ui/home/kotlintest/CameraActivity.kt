package com.company.eve.ui.home.kotlintest

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.company.eve.R
import com.company.eve.databinding.ActivityCameraBinding
import com.company.eve.ui.mypage.FavoriteCharger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CameraActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCameraBinding.inflate(layoutInflater) }
    private lateinit var database: DatabaseReference
    private lateinit var userId: String
    private var isFavorite = false
    private var chargerId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "temporaryUserId"

        intent.getStringExtra("province")?.let { binding.province.text = it }
        intent.getStringExtra("city")?.let { binding.city.text = it }
        intent.getStringExtra("addr")?.let { binding.addr.text = it }
        intent.getStringExtra("statNm")?.let { binding.statNm.text = it }
        intent.getStringExtra("place")?.let { binding.place.text = it }
        intent.getStringExtra("space")?.let { binding.space.text = it }
        intent.getStringExtra("type")?.let { binding.type.text = it }
        intent.getStringExtra("typeS")?.let { binding.typeS.text = it }
        intent.getStringExtra("bnm")?.let { binding.bnm.text = it }
        intent.getStringExtra("busiNm")?.let { binding.busiNm.text = it }
        intent.getStringExtra("output")?.let { binding.output.text = it }
        intent.getStringExtra("Ctype")?.let { binding.Ctype.text = it }
        intent.getStringExtra("limit")?.let { binding.limit.text = it }
        intent.getStringExtra("chgerId")?.let { binding.chgerId.text = it }

        chargerId = intent.getIntExtra("chgerId", 0)
        // 위도와 경도를 표시하지 않도록 주석 처리함
        // val lat = intent.getDoubleExtra("lat", 0.0)
        // val lng = intent.getDoubleExtra("lng", 0.0)
        // binding.location.text = "$lat / $lng"

        binding.close.setOnClickListener {
            this@CameraActivity.finish()
        }

        checkIfFavorite(userId, chargerId)

        binding.favoriteButton.setOnClickListener {
            if (isFavorite) {
                removeFavorite(userId, chargerId)
            } else {
                addFavorite(userId, chargerId)
            }
        }
    }

    private fun addFavorite(userId: String, chargerId: Int) {
        val favoriteRef = database.child("UserAccount").child(userId).child("favorites").child(chargerId.toString())

        val chargerName = intent.getStringExtra("statNm") ?: "Unknown"
        val chargerAddress = intent.getStringExtra("addr") ?: "Unknown"
        val city = intent.getStringExtra("city") ?: "Unknown"
        val limit = intent.getStringExtra("limit") ?: "Unknown"
        val output = intent.getStringExtra("output") ?: "Unknown"
        val statNm = intent.getStringExtra("statNm") ?: "Unknown"
        val typeS = intent.getStringExtra("typeS") ?: "Unknown"
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lng = intent.getDoubleExtra("lng", 0.0)
        val place = intent.getStringExtra("place") ?: "Unknown"
        val province = intent.getStringExtra("province") ?: "Unknown"
        val space = intent.getStringExtra("space") ?: "Unknown"
        val type = intent.getStringExtra("type") ?: "Unknown"
        val Ctype = intent.getStringExtra("Ctype") ?: "Unknown"
        val bnm = intent.getStringExtra("bnm") ?: "Unknown"
        val busiNm = intent.getStringExtra("busiNm") ?: "Unknown"
        val favoriteCharger = FavoriteCharger(
            chargerId,
            chargerName,
            chargerAddress,
            city,
            limit,
            output,
            statNm,
            typeS,
            lat,
            lng,
            place,
            province,
            space,
            type,
            Ctype,
            bnm,
            busiNm
        )

        favoriteRef.setValue(favoriteCharger).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                binding.favoriteButton.setImageResource(R.drawable.on_star)
                isFavorite = true
            } else {
                Toast.makeText(this, "즐겨찾기 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeFavorite(userId: String, chargerId: Int) {
        val favoriteRef = database.child("UserAccount").child(userId).child("favorites").child(chargerId.toString())
        favoriteRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "즐겨찾기에서 제거되었습니다.", Toast.LENGTH_SHORT).show()
                binding.favoriteButton.setImageResource(R.drawable.off_star)
                isFavorite = false
            } else {
                Toast.makeText(this, "즐겨찾기 제거에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIfFavorite(userId: String, chargerId: Int) {
        val favoriteRef = database.child("UserAccount").child(userId).child("favorites").child(chargerId.toString())
        favoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.favoriteButton.setImageResource(R.drawable.on_star)
                    isFavorite = true
                } else {
                    binding.favoriteButton.setImageResource(R.drawable.off_star)
                    isFavorite = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }
}
