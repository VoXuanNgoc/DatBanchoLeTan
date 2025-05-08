package com.example.datban

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = findViewById<DrawerLayout>(R.id.main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.cam, theme)
        toggle.syncState()

//        if(savedInstanceState == null){
//            replaceFragment(BanFragment(),"Đặt Bàn")
//            navigationView.setCheckedItem(R.id.ban)
//        }
        if (savedInstanceState == null) {
            replaceFragment(BanFragment(), "Đặt Bàn")
            val navigationView = findViewById<NavigationView>(R.id.nav_view)
            navigationView.setCheckedItem(R.id.ban)  // Đảm bảo mục 'Đặt Bàn' được chọn mặc định
        }

     //   navigationView = findViewById(R.id.nav_view)


        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.ban -> {
                    replaceFragment(BanFragment(),"Đặt Bàn")
                    true
                }
                R.id.lienhe -> {
                    replaceFragment(LienheFragment(), "Liên Hệ")
                    true
                }
                R.id.add -> {
                    replaceFragment(AddFragment(), "Thêm Bàn")
                    true
                }
                R.id.menu -> {
                    replaceFragment(MenuFragment(),"Menu")
                    true
                }
                R.id.khac -> {
                    replaceFragment(KhacFragment(), "Khác")
                    true
                }
                else -> false
            }
        }
        replaceFragment(BanFragment(),"Đặt Bàn")
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ban -> {
                replaceFragment(BanFragment(), "Đặt Bàn")
                bottomNavigationView.selectedItemId = R.id.ban // Đồng bộ với BottomNavigation
            }
            R.id.lienhe -> {
                replaceFragment(LienheFragment(), "Liên Hệ")
                bottomNavigationView.selectedItemId = R.id.lienhe
            }
            R.id.add -> {
                replaceFragment(AddFragment(), "Thêm Bàn")
                bottomNavigationView.selectedItemId = R.id.add
            }
            R.id.menu -> {
                replaceFragment(MenuFragment(), "Menu")
                bottomNavigationView.selectedItemId = R.id.menu
            }
            R.id.khac -> {
                replaceFragment(KhacFragment(), "Khác")
                bottomNavigationView.selectedItemId = R.id.khac
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun replaceFragment(fragment: Fragment, title: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.commit()
        supportActionBar?.title = title
    }


    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers()
        } else{
            super.onBackPressed() // gọi phương thức super để xử lý hành động mặc định
        }
    }
}