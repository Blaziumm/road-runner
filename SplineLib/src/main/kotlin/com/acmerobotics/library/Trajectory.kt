package com.acmerobotics.library

class Trajectory(segments: List<TrajectorySegment> = listOf()) {
    val segments: MutableList<TrajectorySegment> = segments.toMutableList()

    fun duration() = segments.map { it.duration() }.sum()

    operator fun get(time: Double): Pose2d {
        var remainingTime = time
        for (segment in segments) {
            if (remainingTime <= segment.duration()) {
                return segment[remainingTime]
            }
            remainingTime -= segment.duration()
        }
        return segments.last()[segments.last().duration()]
    }

    fun velocity(time: Double): Pose2d {
        var remainingTime = time
        for (segment in segments) {
            if (remainingTime <= segment.duration()) {
                return segment.velocity(remainingTime)
            }
            remainingTime -= segment.duration()
        }
        return segments.last().velocity(segments.last().duration())
    }

    fun acceleration(time: Double): Pose2d {
        var remainingTime = time
        for (segment in segments) {
            if (remainingTime <= segment.duration()) {
                return segment.acceleration(remainingTime)
            }
            remainingTime -= segment.duration()
        }
        return segments.last().acceleration(segments.last().duration())
    }
}