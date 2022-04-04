LD2NL
=========

### About
This project aims at delivering a framework for converting Linked Data related concepts to Natural language.

<!---
### Required files

Please download the <a href="https://hobbitdata.informatik.uni-leipzig.de/LD2NL/NIHLexicon.zip">NIHLexion</a> and unzip it within the resources folder of SPARQL2NL (LD2NL/SPARQL2NL/src/main/resources/)
-->


### Structure

LD2NL currently consists of the following modules:

<!--- 1. Assess - Automatic Self Assessment -->
1. Avatar -  Entity summarization
1. SPARQL2NL - Convert SPARQL queries into natural language
1. Triple2NL - Convert triples into natural language
1. OWL2NL - Convert OWL into natural language
1. Evaluation
1. Controller

Each module has its own README.md file.

### Compile LD2NL
`mvn -T 1C clean install test`

### Support and Feedback
If you need help or you have questions do not hesitate to write an issue in the right sidebar.

### How to cite
```Tex

@InProceedings{Ngonga2019,
  author       = {Axel-Cyrille {Ngonga Ngomo}, Diego Moussallem and Lorenz Bühman},
  title        = {{A Holistic Natural Language Generation Framework for the Semantic Web}},
  booktitle    = {Proceedings of the International Conference Recent Advances in Natural Language Processing},
  year         = {2019},
  pages        = {8},
  organization={ACL (Association for Computational Linguistics)}
}
```
