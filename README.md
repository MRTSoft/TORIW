# Please note that this is a **WORK IN PROGRESS** #

# Academic grade web search engine #

This project aims to implement all of the main components of a search engine
* Web crawler
* Indexer
* Search service

At the moment we created a small part of the indexer.

# TODO:

1. ~~Add porter from a free API~~ 
1. Use Stanford NLP framework [nlp.stanford.edu](nlp.stanford.edu)
1. Use MongoDB for saving direct/indirect indexes
1. Modify Search function to include Cosine distance
    1. ~~Use binary search to determine a subset of relevant documents~~
    2. Use cosine distance to determine the most relevant document of that subset
1. Create a config file that includes (data + mongoDB config); 