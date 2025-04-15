//Properties
//Height (Int)
//PreviousBlock (HashMap)
//Transaction[] Transaction
//Hash (String)
//Metodo
//Create() handle special case for block 0 as we cannot reference the PreviousBlock.
//


package it.tff.francocoin;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class Block {
  private int height;
  private HashMap<String, Object> PreviousBlock;
  private Transaction[] transactions;
  private String hash;
  private BigInteger publicKey;
  private String signature;

  //This is ported 1:1 from C's #define syntax and customs
  final BigInteger FNV_OFFSET = new BigInteger("144066263297769815596495629667062367629");
  final BigInteger FNV_PRIME = new BigInteger("309485009821345068724781371"); 

  public Block(){}

  public Block(int height, HashMap<String, Object> previousBlock, 
                Transaction[] transactions, String hash) {
        this.height = height;
        this.PreviousBlock = previousBlock;
        this.transactions = transactions;
        this.hash = hash;
    }


  public static Block create(int height, HashMap<String, Object> PreviousBlock, 
      Transaction[] transactions) {
    
    Block block = new Block();
    block.height = height;
    block.transactions = transactions;

    if (height == 0) {
      block.PreviousBlock= null;
    } else {
      block.PreviousBlock = PreviousBlock;
    }
    
    block.hash = generateHash(block);
    return block;

  }
  //Cryptographic implementation with Blake2s Hashing.
  //It is unnecessary
  //(Check Wikipedia for Fowler-Noll-Vo hashing)

/*
  private static String generateHash(Block block) {
    String prevBlockData = (block.PreviousBlock != null)? block.PreviousBlock.toString(): "";
    String transactionData = (block.transactions != null)? Arrays.toString(block.transactions) : "";
    String data = block.height + prevBlockData + transactionData;
    byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

    //Blake2s:
    Blake2sDigest digest = new Blake2sDigest();
    digest.update(dataBytes, 0, dataBytes.length);

    //Calculate the hash:
    byte[] interHash = new byte[digest.getDigestSize()];
    digest.doFinal(interHash, 0);

    return Hex.toHexString(interHash);
  }
*/


  /*
    algorithm fnv-1a is
    hash := FNV_offset_basis

    for each byte_of_data to be hashed do
        hash := hash XOR byte_of_data
        hash := hash × FNV_prime

    return hash 
    */
  private static String generateHash(Block block) {
    String prevBlockData = (block.PreviousBlock != null)? block.PreviousBlock.toString(): "";
    String transactionData = (block.transactions != null)? Arrays.toString(block.transactions) : "";
    String data = block.height + prevBlockData + transactionData;
    byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

    //Do the Hashing
    //This is ported 1:1 from C's #define syntax and customs
    final BigInteger FNV_OFFSET = new BigInteger("144066263297769815596495629667062367629");
    final BigInteger FNV_PRIME = new BigInteger("309485009821345068724781371"); 
  
    BigInteger hash = FNV_OFFSET;

    for (byte b : dataBytes) {
        hash = hash.xor(BigInteger.valueOf(b & 0xff));
        hash = hash.multiply(FNV_PRIME);
    }

    return hash.toString(16);


  }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public HashMap<String, Object> getPreviousBlock() {
        return PreviousBlock;
    }

    public void setPreviousBlock(HashMap<String, Object> previousBlock) {
        this.PreviousBlock = previousBlock;
    }

    public Transaction[] getTransactions() {
        return transactions;
    }

    public void setTransactions(Transaction[] transactions) {
        this.transactions = transactions;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

}




