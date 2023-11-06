package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModelFactory(
                requireNotNull(this.activity).application,
            ),
        )[(MainViewModel::class.java)]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        val adapter = MainAdapter(
            AsteroidListener { asteroid ->
                viewModel.onAsteroidClicked(asteroid)
                binding.asteroidRecycler.contentDescription =
                    getString(R.string.asteroid_selected_content_description, asteroid.codename)
            },
        )
        binding.asteroidRecycler.adapter = adapter

        viewModel.navigateToAsteroidDetails.observe(viewLifecycleOwner) { asteroidId ->
            asteroidId?.let {
                this.findNavController().navigate(
                    MainFragmentDirections.actionShowDetail(asteroidId),
                )
                viewModel.onAsteroidClickedComplete()
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.changeFilter(
            when (item.itemId) {
                R.id.show_all_menu -> AsteroidFilter.WEEK
                R.id.show_buy_menu -> AsteroidFilter.SAVED
                else -> AsteroidFilter.TODAY
            },
        )
        return true
    }
}
