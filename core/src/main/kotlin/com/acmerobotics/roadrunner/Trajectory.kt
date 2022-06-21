@file:JvmName("Trajectory")

package com.acmerobotics.roadrunner

// TODO: do we even need this class?
// I'm less and less happy with its existence
data class DisplacementTrajectory(
    @JvmField
    val path: PosePath,
    @JvmField
    val dispProfile: DisplacementProfile,
) {
    operator fun get(s: Double, n: Int) = path[s, n].reparam(dispProfile[s])

    // TODO: why do we even need projection derivatives anyway? just wire the profile kinematics straight through
    fun project(query: Position2Dual<Time>, init: Double) =
        project(path, query.value(), init).let { s ->
            val r = path[s, 3].translation
                .bind()
                .reparam(dispProfile[s])

            val d = query - r
            val drds = r.tangentVec()
            val d2rds2 = drds.drop(1)

            val dsdt = (query.tangentVec() dot drds) / ((d dot d2rds2) - 1.0)

            dsdt.addFirst(s)
        }
}

data class TimeTrajectory @JvmOverloads constructor(
    @JvmField
    val dispTrajectory: DisplacementTrajectory,
    @JvmField
    val timeProfile: TimeProfile = TimeProfile(dispTrajectory.dispProfile),
) {
    operator fun get(t: Double, n: Int) = timeProfile[t].let { s ->
        dispTrajectory.path[s.value(), n].reparam(s)
    }

    // TODO: are either of these methods necessary?
    // they create pressure on other interfaces to be (Displacement|Time)Trajectory agnostic
    fun getByDisp(s: Double, n: Int) = dispTrajectory[s, n]
    fun project(query: Position2Dual<Time>, init: Double) = dispTrajectory.project(query, init)
}

// TODO: merge max vel/accel functions
