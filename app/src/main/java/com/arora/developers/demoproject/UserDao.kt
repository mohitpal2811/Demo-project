package com.arora.developers.demoproject

import androidx.room.*

@Dao
interface UserDao {

    @Insert
    fun addUser(user: User)

    @Update
    fun updateUser(user: User)

    @Delete
    fun delete(user: User)


    @Query("select * from user_table order by fiId ASC LIMIT :fiLower, :fiUpper")
    fun readAllData(fiUpper:Int,fiLower :Int ): List<User>

    @Query("SELECT * FROM user_table WHERE fsFirstName LIKE '%' || :search || '%' Or fsEmail LIKE '%' || :search || '%'")
    fun search(search: String?): List<User>
}
