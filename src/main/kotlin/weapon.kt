import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File

@Serializable
class Weapon(
    var level: String,
    var cost: String,
    var ID: String? = null,
    var name: String,
){
    var enemiesSlain = mutableListOf<String>()
    var owners = mutableListOf<String>()

    fun printStats(){
        print("Level: $level \n" +
                "Cost: $cost \n" +
                "Id: $ID \n" +
                "Name: $name \n" +
                "Enemies Slain: $enemiesSlain \n" +
                "Owners: $owners \n"
        )
    }
}

fun createID(): String{
    val idNumber = 5
    val length = 16
    val iD = java.lang.StringBuilder(length)
//    val hexChars = "0123456789ABCDEFGH"

    for (decimal_place in 1 until length){
        if(idNumber.floorDiv(16) >= length - decimal_place){
            iD.append(idNumber % 16)
        }
        else{
            iD.append("0")
        }
    }

    return iD.toString()
}

fun addWeaponBlock(weapon: Weapon, blockChain: BlockChain){
    val jsonData: String = Json.encodeToString(weapon)
    blockChain.addBlock(mineBlock(Block(blockChain.lastHash, jsonData), blockChain.difficulty))
}

fun createSample(){
    val testWeapon1 = Weapon("10", "20", name = "dagger")
    val testWeapon2 = Weapon("20", "40", name = "sword")
    val testWeapon3 = Weapon("30", "80", name = "axe")

    testWeapon1.enemiesSlain.add("Balgroth the weak")
    testWeapon2.enemiesSlain.add("Dorum the nasty")
    testWeapon3.enemiesSlain.add("Filbur the defenseless")

    testWeapon1.owners.add("Pipsqueak")
    testWeapon2.owners.add("Borum")
    testWeapon3.owners.add("DeathSkull")

    testWeapon1.ID = blockChain.makeNewId().toString()
    testWeapon2.ID = blockChain.makeNewId().toString()
    testWeapon3.ID = blockChain.makeNewId().toString()

    addWeaponBlock(testWeapon1, blockChain)
    addWeaponBlock(testWeapon2, blockChain)
    addWeaponBlock(testWeapon3, blockChain)

    val jsonBlockChain = Json.encodeToString(blockChain)
    File("src/main/JSON/BlockChain.json").writeText(jsonBlockChain)
    blockChain.printBlocks()
}
