import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest
import java.sql.Timestamp


val blockChain = Json.decodeFromString<BlockChain>(File("src/main/JSON/BlockChain.json").readText())
// Holds all of the blocks that we will create and store
@Serializable
class BlockChain {
    var blocks = mutableListOf<Block>()
    var lastHash: String = "GENESISBLOCK"
    var difficulty: Int = 3
    var highestID: Int = 0

    fun addBlock(block: Block) {
        if (validBlock(block)) {
            this.blocks.add(block)
            this.lastHash = block.blockHash
        } else {
            println("Invalid block hash")
        }

    }

    fun printBlocks() {
        var i = 0
        for (block in blocks) {

            print("Block $i\n")
            print(
                "\tPrevious Hash: ${block.previousHash}\n" +
                        "\tData: ${block.data}\n" +
                        "\tTime mined: ${block.timeStamp}\n" +
                        "\tBlock Hash: ${block.blockHash}\n"
            )
            i += 1
        }
    }

    private fun validBlock(block: Block): Boolean {
        if (block.blockHash == getHash(block))
            return true

        print("${block.blockHash}, ${getHash(block)}")
        return false
    }


    fun blockChainIsValid(): Boolean {
        //    TODO: Add a way to check if the hashes match up Will probably use the block nonce to make sure hash isn't made up
        // as well
        for (x in 1 until blocks.size) {
            if (blocks[x].previousHash != blocks[x - 1].blockHash) {
                return false
            }
        }

        return true
    }

    fun makeNewId(): Int{
        highestID+=1
        return highestID
    }
}

//val blockChain = BlockChain()

// All of the data that we want to store inside of our blocks
@Serializable
class Block(
    val previousHash: String,
    val data: String,
    var timeStamp: String = "",
    var nonce: String = "",
    var blockHash: String = ""
)

// uses SHA256 to digest the data and return a number
fun getHash(block: Block): String {
    val data = arrayOf(block.previousHash, block.timeStamp, block.nonce)
    var allOfTheData = ""
    val hexChars = "0123456789ABCDEF"

    //	Make all of the data one long string
    for (item in data) allOfTheData += item

    val bytes = MessageDigest
        .getInstance("sha256") // Choose algorithm
        .digest(allOfTheData.toByteArray()) // Digest the data

    val hash = StringBuilder(bytes.size * 2)
    bytes.forEach {
        val i = it.toInt()
        hash.append(hexChars[i shr 4 and 0x0f])
        hash.append(hexChars[i and 0x0f])
    }

    return hash.toString()
}

// Create the very first block
//fun getGenesisBlock(): Block {
//    val genBlock = Block("0".repeat(32), "Empty")
//
//    return mineBlock(genBlock, 0)
//}


fun firstNumbersEqual(hash: String, numberOfZeros: Int): Boolean {
    for (x in 0 until numberOfZeros) {
        if (hash[x] != '0')
            return false
    }

    return true
}

fun mineBlock(block: Block, difficulty: Int): Block {
    // Go through the block until it has x amount of zeros in the front of it's hash
    block.blockHash = getHash(block)

    while (!firstNumbersEqual(block.blockHash, difficulty)) {
        block.timeStamp = Timestamp(System.currentTimeMillis()).toString()
        block.nonce = Math.random().toString()
        block.blockHash = getHash(block)
    }

//    block.nonce = Math.random().toString()
    print("Block has been mined with a hash of: ${block.blockHash}\n")
    return block
}

fun createSampleBlocks(blockChain: BlockChain) {
    val sampleBlock = mineBlock(Block(blockChain.lastHash, "01".repeat(16)), blockChain.difficulty)
    blockChain.addBlock(sampleBlock)

    val minedBlock = mineBlock(Block(blockChain.lastHash, "Porter"), blockChain.difficulty)
    blockChain.addBlock(minedBlock)
}

fun getWeapon(){
    println("What is the weapon name?")
    val name = readLine()

    for (block in blockChain.blocks){
//        println(block.data)
        try {
            val weapon = Json.decodeFromString<Weapon>(block.data)
            if(weapon.name == name){
                weapon.printStats()
            }
        } catch (t: Throwable){
            println("Data empty moving on to next block")
        }

    }
}

fun getInput(): Boolean {
    print(
        "What would you like to do?\n" +
                "\t(p): Print block chain \n" +
                "\t(w): get weapon \n" +
                "\t(q): quit program\n"
    )

    val decision = readLine()
    when (decision) {
        "p" -> blockChain.printBlocks()
        "w" -> getWeapon()
        "q" -> return false
    }

    return true
}


fun main() {
    // Load up json data into blockChain
//    createSample()

    while (getInput()){ }

    val jsonBlockChain = Json.encodeToString(blockChain)
    File("src/main/JSON/BlockChain.json").writeText(jsonBlockChain)

}