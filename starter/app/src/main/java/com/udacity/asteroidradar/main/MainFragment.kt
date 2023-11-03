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
import com.udacity.asteroidradar.models.Asteroid

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[(MainViewModel::class.java)]
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
            },
        )
        binding.asteroidRecycler.adapter = adapter
        adapter.submitList(getDummyAsteroidList())

        setupObserver()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

    private fun setupObserver() {
        viewModel.navigateToAsteroidDetails.observe(viewLifecycleOwner) { asteroidId ->
            asteroidId?.let {
                this.findNavController().navigate(
                    MainFragmentDirections.actionShowDetail(asteroidId),
                )
                viewModel.onAsteroidClickedComplete()
            }
        }
    }

    private fun getDummyAsteroidList(): List<Asteroid> {
        val list = mutableListOf<Asteroid>()
        for (x in 1..10) {
            list.add(
                Asteroid(
                    id = 123456789L,
                    codename = "Kepler 362B",
                    closeApproachDate = "2023-05-15",
                    absoluteMagnitude = 5000.20,
                    estimatedDiameter = 1500.25,
                    relativeVelocity = 2600.00,
                    distanceFromEarth = 15000000.00,
                    isPotentiallyHazardous = false,
                ),
            )
        }
        return list
    }
}
