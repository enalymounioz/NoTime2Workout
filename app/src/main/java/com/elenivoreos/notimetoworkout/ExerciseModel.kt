package com.elenivoreos.notimetoworkout

class ExerciseModel (
    private var id:Int,
    private var name:String,
    private var image: Int,
    private var isComplete: Boolean,
    private var isSelected: Boolean
        )
{
    fun getId(): Int{
        return id
    }
    fun setId(id:Int) {
        this.id = id
    }

    fun getName(): String{
        return name
    }
    fun setName(name:String) {
        this.name = name
    }

    fun getImage(): Int{
        return image
    }
    fun setImage(image:Int) {
        this.image = image
    }

    fun getIsComplete(): Boolean{
        return isComplete
    }
    fun setIsComplete() {
        this.isComplete = isComplete
    }

    fun getIsSelected(): Boolean{
        return isSelected
    }
    fun setIsSelected() {
        this.isSelected = isSelected
    }

}