package com.pylons.wallet.core.fixtures

val gamerule = """
    {
  "id": "onboard",
  "coinsOut": [
    {
      "id": "gold",
      "count": 100
    }
  ],
  "itemsOut": [
    {
      "doubleConstraints": {},
      "longConstraints": {
        "level": [
          {
            "value": 1,
            "mode": "EXACT_MATCH"
          }
        ],
        "currentXp": [
          {
            "value": 0,
            "mode": "EXACT_MATCH"
          }
        ],
        "morale": [
          {
            "value": 100,
            "mode": "EXACT_MATCH"
          }
        ]
      },
      "stringConstraints": {
        "adventurerClass": [
          {
            "value": "MeleeDPS",
            "mode": "ONE_OF_THESE"
          },
          {
            "value": "Tank",
            "mode": "ONE_OF_THESE"
          },
          {
            "value": "Healer",
            "mode": "ONE_OF_THESE"
          }
        ],
        "schema": [
          {
            "value": "adventurer",
            "mode": "EXACT_MATCH"
          }
        ],
        "portraitType": [
          {
            "value": "TestPortrait",
            "mode": "ONE_OF_THESE"
          },
          {
            "value": "EvenMoreTestPortrait",
            "mode": "ONE_OF_THESE"
          }
        ],
        "name": [
          {
            "value": "An adventurer",
            "mode": "ONE_OF_THESE"
          },
          {
            "value": "Another adventurer",
            "mode": "ONE_OF_THESE"
          }
        ]
      }
    },
    {
      "doubleConstraints": {},
      "longConstraints": {
        "level": [
          {
            "value": 1,
            "mode": "EXACT_MATCH"
          }
        ],
        "currentXp": [
          {
            "value": 0,
            "mode": "EXACT_MATCH"
          }
        ],
        "morale": [
          {
            "value": 100,
            "mode": "EXACT_MATCH"
          }
        ]
      },
      "stringConstraints": {
        "adventurerClass": [
          {
            "value": "MeleeDPS",
            "mode": "ONE_OF_THESE"
          },
          {
            "value": "Tank",
            "mode": "ONE_OF_THESE"
          },
          {
            "value": "Healer",
            "mode": "ONE_OF_THESE"
          }
        ],
        "schema": [
          {
            "value": "adventurer",
            "mode": "EXACT_MATCH"
          }
        ],
        "portraitType": [
          {
            "value": "TestPortrait",
            "mode": "ONE_OF_THESE"
          },
          {
            "value": "EvenMoreTestPortrait",
            "mode": "ONE_OF_THESE"
          }
        ],
        "name": [
          {
            "value": "An adventurer",
            "mode": "ONE_OF_THESE"
          },
          {
            "value": "Another adventurer",
            "mode": "ONE_OF_THESE"
          }
        ]
      }
    },
    {
      "doubleConstraints": {},
      "longConstraints": {
        "level": [
          {
            "value": 1,
            "mode": "EXACT_MATCH"
          }
        ],
        "currentXp": [
          {
            "value": 0,
            "mode": "EXACT_MATCH"
          }
        ],
        "morale": [
          {
            "value": 100,
            "mode": "EXACT_MATCH"
          }
        ]
      },
      "stringConstraints": {
        "adventurerClass": [
          {
            "value": "MeleeDPS",
            "mode": "ONE_OF_THESE"
          },
          {
            "value": "Tank",
            "mode": "ONE_OF_THESE"
          },
          {
            "value": "Healer",
            "mode": "ONE_OF_THESE"
          }
        ],
        "schema": [
          {
            "value": "adventurer",
            "mode": "EXACT_MATCH"
          }
        ],
        "portraitType": [
          {
            "value": "TestPortrait",
            "mode": "ONE_OF_THESE"
          },
          {
            "value": "EvenMoreTestPortrait",
            "mode": "ONE_OF_THESE"
          }
        ],
        "name": [
          {
            "value": "An adventurer",
            "mode": "ONE_OF_THESE"
          },
          {
            "value": "Another adventurer",
            "mode": "ONE_OF_THESE"
          }
        ]
      }
    }
  ],
  "lootTables": [
    {
      "entries": [
        {
          "items": [
            {
              "doubleConstraints": {},
              "longConstraints": {
                "expiryDate": [
                  {
                    "value": 0,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questLevel": [
                  {
                    "value": 1,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "blocksUntilCompletion": [
                  {
                    "value": 1,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 6,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "stringConstraints": {
                "state": [
                  {
                    "value": "notYetStarted",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "schema": [
                  {
                    "value": "contractTicket",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questName": [
                  {
                    "value": "A test quest",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Another test quest",
                    "mode": "EXACT_MATCH"
                  },
                  {
                    "value": "Yet another test quest",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questType": [
                  {
                    "value": "Exploration",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "recipe": [
                  {
                    "value": "debug_BasicQuest",
                    "mode": "EXACT_MATCH"
                  }
                ]
              }
            }
          ],
          "likelihood": 3
        },
        {
          "items": [
            {
              "doubleConstraints": {
                "ENEMY_GOBLIN": [
                  {
                    "value": 0.75,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 1.25,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "longConstraints": {
                "expiryDate": [
                  {
                    "value": 0,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questLevel": [
                  {
                    "value": 1,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "blocksUntilCompletion": [
                  {
                    "value": 1,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 6,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "stringConstraints": {
                "state": [
                  {
                    "value": "notYetStarted",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "schema": [
                  {
                    "value": "contractTicket",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questName": [
                  {
                    "value": "Fightan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Battlan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Warran'",
                    "mode": "ONE_OF_THESE"
                  }
                ],
                "questType": [
                  {
                    "value": "Combat",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "recipe": [
                  {
                    "value": "debug_BasicQuest",
                    "mode": "EXACT_MATCH"
                  }
                ]
              }
            }
          ],
          "likelihood": 1
        },
        {
          "items": [
            {
              "doubleConstraints": {
                "ENEMY_KOBOLD": [
                  {
                    "value": 0.75,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 1.25,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "longConstraints": {
                "expiryDate": [
                  {
                    "value": 0,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questLevel": [
                  {
                    "value": 1,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "blocksUntilCompletion": [
                  {
                    "value": 1,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 6,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "stringConstraints": {
                "state": [
                  {
                    "value": "notYetStarted",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "schema": [
                  {
                    "value": "contractTicket",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questName": [
                  {
                    "value": "Fightan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Battlan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Warran'",
                    "mode": "ONE_OF_THESE"
                  }
                ],
                "questType": [
                  {
                    "value": "Combat",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "recipe": [
                  {
                    "value": "debug_BasicQuest",
                    "mode": "EXACT_MATCH"
                  }
                ]
              }
            }
          ],
          "likelihood": 1
        },
        {
          "items": [
            {
              "doubleConstraints": {
                "ENEMY_SLIME": [
                  {
                    "value": 0.75,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 1.25,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "longConstraints": {
                "expiryDate": [
                  {
                    "value": 0,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questLevel": [
                  {
                    "value": 1,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "blocksUntilCompletion": [
                  {
                    "value": 1,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 6,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "stringConstraints": {
                "state": [
                  {
                    "value": "notYetStarted",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "schema": [
                  {
                    "value": "contractTicket",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questName": [
                  {
                    "value": "Fightan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Battlan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Warran'",
                    "mode": "ONE_OF_THESE"
                  }
                ],
                "questType": [
                  {
                    "value": "Combat",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "recipe": [
                  {
                    "value": "debug_BasicQuest",
                    "mode": "EXACT_MATCH"
                  }
                ]
              }
            }
          ],
          "likelihood": 1
        }
      ]
    },
    {
      "entries": [
        {
          "items": [
            {
              "doubleConstraints": {},
              "longConstraints": {
                "expiryDate": [
                  {
                    "value": 0,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questLevel": [
                  {
                    "value": 1,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "blocksUntilCompletion": [
                  {
                    "value": 1,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 6,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "stringConstraints": {
                "state": [
                  {
                    "value": "notYetStarted",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "schema": [
                  {
                    "value": "contractTicket",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questName": [
                  {
                    "value": "A test quest",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Another test quest",
                    "mode": "EXACT_MATCH"
                  },
                  {
                    "value": "Yet another test quest",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questType": [
                  {
                    "value": "Exploration",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "recipe": [
                  {
                    "value": "debug_BasicQuest",
                    "mode": "EXACT_MATCH"
                  }
                ]
              }
            }
          ],
          "likelihood": 3
        },
        {
          "items": [
            {
              "doubleConstraints": {
                "ENEMY_GOBLIN": [
                  {
                    "value": 0.75,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 1.25,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "longConstraints": {
                "expiryDate": [
                  {
                    "value": 0,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questLevel": [
                  {
                    "value": 1,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "blocksUntilCompletion": [
                  {
                    "value": 1,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 6,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "stringConstraints": {
                "state": [
                  {
                    "value": "notYetStarted",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "schema": [
                  {
                    "value": "contractTicket",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questName": [
                  {
                    "value": "Fightan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Battlan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Warran'",
                    "mode": "ONE_OF_THESE"
                  }
                ],
                "questType": [
                  {
                    "value": "Combat",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "recipe": [
                  {
                    "value": "debug_BasicQuest",
                    "mode": "EXACT_MATCH"
                  }
                ]
              }
            }
          ],
          "likelihood": 1
        },
        {
          "items": [
            {
              "doubleConstraints": {
                "ENEMY_KOBOLD": [
                  {
                    "value": 0.75,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 1.25,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "longConstraints": {
                "expiryDate": [
                  {
                    "value": 0,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questLevel": [
                  {
                    "value": 1,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "blocksUntilCompletion": [
                  {
                    "value": 1,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 6,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "stringConstraints": {
                "state": [
                  {
                    "value": "notYetStarted",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "schema": [
                  {
                    "value": "contractTicket",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questName": [
                  {
                    "value": "Fightan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Battlan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Warran'",
                    "mode": "ONE_OF_THESE"
                  }
                ],
                "questType": [
                  {
                    "value": "Combat",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "recipe": [
                  {
                    "value": "debug_BasicQuest",
                    "mode": "EXACT_MATCH"
                  }
                ]
              }
            }
          ],
          "likelihood": 1
        },
        {
          "items": [
            {
              "doubleConstraints": {
                "ENEMY_SLIME": [
                  {
                    "value": 0.75,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 1.25,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "longConstraints": {
                "expiryDate": [
                  {
                    "value": 0,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questLevel": [
                  {
                    "value": 1,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "blocksUntilCompletion": [
                  {
                    "value": 1,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 6,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "stringConstraints": {
                "state": [
                  {
                    "value": "notYetStarted",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "schema": [
                  {
                    "value": "contractTicket",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questName": [
                  {
                    "value": "Fightan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Battlan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Warran'",
                    "mode": "ONE_OF_THESE"
                  }
                ],
                "questType": [
                  {
                    "value": "Combat",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "recipe": [
                  {
                    "value": "debug_BasicQuest",
                    "mode": "EXACT_MATCH"
                  }
                ]
              }
            }
          ],
          "likelihood": 1
        }
      ]
    },
    {
      "entries": [
        {
          "items": [
            {
              "doubleConstraints": {},
              "longConstraints": {
                "expiryDate": [
                  {
                    "value": 0,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questLevel": [
                  {
                    "value": 1,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "blocksUntilCompletion": [
                  {
                    "value": 1,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 6,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "stringConstraints": {
                "state": [
                  {
                    "value": "notYetStarted",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "schema": [
                  {
                    "value": "contractTicket",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questName": [
                  {
                    "value": "A test quest",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Another test quest",
                    "mode": "EXACT_MATCH"
                  },
                  {
                    "value": "Yet another test quest",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questType": [
                  {
                    "value": "Exploration",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "recipe": [
                  {
                    "value": "debug_BasicQuest",
                    "mode": "EXACT_MATCH"
                  }
                ]
              }
            }
          ],
          "likelihood": 3
        },
        {
          "items": [
            {
              "doubleConstraints": {
                "ENEMY_GOBLIN": [
                  {
                    "value": 0.75,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 1.25,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "longConstraints": {
                "expiryDate": [
                  {
                    "value": 0,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questLevel": [
                  {
                    "value": 1,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "blocksUntilCompletion": [
                  {
                    "value": 1,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 6,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "stringConstraints": {
                "state": [
                  {
                    "value": "notYetStarted",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "schema": [
                  {
                    "value": "contractTicket",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questName": [
                  {
                    "value": "Fightan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Battlan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Warran'",
                    "mode": "ONE_OF_THESE"
                  }
                ],
                "questType": [
                  {
                    "value": "Combat",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "recipe": [
                  {
                    "value": "debug_BasicQuest",
                    "mode": "EXACT_MATCH"
                  }
                ]
              }
            }
          ],
          "likelihood": 1
        },
        {
          "items": [
            {
              "doubleConstraints": {
                "ENEMY_KOBOLD": [
                  {
                    "value": 0.75,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 1.25,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "longConstraints": {
                "expiryDate": [
                  {
                    "value": 0,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questLevel": [
                  {
                    "value": 1,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "blocksUntilCompletion": [
                  {
                    "value": 1,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 6,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "stringConstraints": {
                "state": [
                  {
                    "value": "notYetStarted",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "schema": [
                  {
                    "value": "contractTicket",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questName": [
                  {
                    "value": "Fightan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Battlan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Warran'",
                    "mode": "ONE_OF_THESE"
                  }
                ],
                "questType": [
                  {
                    "value": "Combat",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "recipe": [
                  {
                    "value": "debug_BasicQuest",
                    "mode": "EXACT_MATCH"
                  }
                ]
              }
            }
          ],
          "likelihood": 1
        },
        {
          "items": [
            {
              "doubleConstraints": {
                "ENEMY_SLIME": [
                  {
                    "value": 0.75,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 1.25,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "longConstraints": {
                "expiryDate": [
                  {
                    "value": 0,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questLevel": [
                  {
                    "value": 1,
                    "mode": "EXACT_MATCH"
                  }
                ],
                "blocksUntilCompletion": [
                  {
                    "value": 1,
                    "mode": "NUM_MORE_THAN"
                  },
                  {
                    "value": 6,
                    "mode": "NUM_LESS_THAN"
                  }
                ]
              },
              "stringConstraints": {
                "state": [
                  {
                    "value": "notYetStarted",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "schema": [
                  {
                    "value": "contractTicket",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "questName": [
                  {
                    "value": "Fightan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Battlan'",
                    "mode": "ONE_OF_THESE"
                  },
                  {
                    "value": "Warran'",
                    "mode": "ONE_OF_THESE"
                  }
                ],
                "questType": [
                  {
                    "value": "Combat",
                    "mode": "EXACT_MATCH"
                  }
                ],
                "recipe": [
                  {
                    "value": "debug_BasicQuest",
                    "mode": "EXACT_MATCH"
                  }
                ]
              }
            }
          ],
          "likelihood": 1
        }
      ]
    }
  ]
}
""".trimIndent()