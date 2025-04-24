package com.data.app.presentation.my

import androidx.lifecycle.ViewModel
import com.data.app.data.Follow

class FollowViewModel:ViewModel() {
    val followerList = listOf(
        Follow(name = "Emily Johnson", id = "emily_j", isFollow = true),
        Follow(name = "Liam Smith", id = "liam_smith23", isFollow = true),
        Follow(name = "Olivia Brown", id = "olivia_b", isFollow = true),
        Follow(name = "Noah Davis", id = "noah_d", isFollow = true),
        Follow(name = "Ava Wilson", id = "ava_w", isFollow = true),
        Follow(name = "James Miller", id = "james_mil", isFollow = true),
        Follow(name = "Sophia Moore", id = "sophia.moore", isFollow = true),
        Follow(name = "Benjamin Taylor", id = "ben_tay", isFollow = true),
        Follow(name = "Isabella Anderson", id = "bella_anderson", isFollow = true),
        Follow(name = "Lucas Thomas", id = "lucas_t", isFollow = true)
    )

    val followingList = listOf(
        Follow(name = "Mason Martin", id = "mason_m", isFollow = true),
        Follow(name = "Mia White", id = "mia_white88", isFollow = false),
        Follow(name = "Ethan Jackson", id = "ethan_jx", isFollow = true),
        Follow(name = "Harper Lee", id = "harper.lee", isFollow = false),
        Follow(name = "Logan Harris", id = "logan_h", isFollow = true),
        Follow(name = "Aria Clark", id = "aria_c", isFollow = false),
        Follow(name = "Elijah Lewis", id = "elijah_l", isFollow = true),
        Follow(name = "Grace Robinson", id = "grace_rb", isFollow = true),
        Follow(name = "Henry Walker", id = "henrywalk", isFollow = false),
        Follow(name = "Chloe Hall", id = "chloe_hall", isFollow = true)
    )
}