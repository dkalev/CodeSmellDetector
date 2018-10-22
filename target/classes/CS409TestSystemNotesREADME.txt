Bloaters
- Long Method - BarnsleyFern (createFern)
- Long Method - floodFill
- Primitive Obsession / Data clumps - plot, drawLine and paintComponent in BresenhamPanel
- Long Parameter List - ManOrBoy - A
- Large Class - see Grid in Switch

Abusers
- Temporary field - BarnsleyFernTwo
- Switch - MorpionSolitairePanel switches on an enum
Also Grid is Large class and contains long methods, data classes and long parameter lists!
- Refused bequest - two cases in ChqAcc and SavingsAcc
ACwDI - Alternative classes with different interfaces. There is clearly a lot of commonality between Underling and Manager that should be factored out into an inheritance hierarchy. These two are also good candidates for data classes.

Dispensibles
- Lazy or Data Classes - Point and Triple in Cipolla and Message Chains in Cipolla
- Lazy or Data Class - Node in Eertree (also List is a long method)
- There is also duplicate code (at least) between the two BarnsleyFern implementations
- Dead Code in Luhn (2 cases)
- Duplicate and dead code in Test
- Speculative Generality - SeasonalStockItem and ValuableStockItem are examples of this in this package

Couplers
 - Message chains - Munchausen
 - Message chains - NBodySim
 - Feature Envy - FeatureEnvy Customer and Phone, and Item and Basket
   And Item and Phone are also Data Classes
 - Inappropriate Intimacy - Huffman code accesses the files of HuffmanLeaf, HuffmanNode and HuffmanTree (quite a weak example)
- Middle Man - AccountManager (plus Account is a lazy class)

FalsePositives
 - should be nothing to see there
 - switch in box the compass






