# 🤖 function-calling-assistant

> Assistant LLM capable d’interagir avec des fonctions backend simulées (tâches, rappels, suppression).

Ce projet met en œuvre le **function calling** : une capacité des modèles de langage à **déclencher dynamiquement des fonctions** dans ton application, avec des arguments déduits automatiquement à partir du langage naturel.

---

## 🧠 Qu’est-ce que le Function Calling ?

Le **Function Calling** permet à un LLM de :
1. **Comprendre une intention utilisateur** exprimée en langage naturel.
2. **Choisir une fonction** que tu as déclarée à l’avance.
3. **Générer un appel structuré** avec les bons paramètres (`arguments`).
4. **Laisser ton backend exécuter la logique réelle** (BDD, API, etc.).
5. **Intégrer le résultat dans une réponse finale**, toujours en langage naturel.

### Exemple :

**Utilisateur : "Ajoute un rappel pour appeler Paul demain à 10h"**

↓ Modèle : { "name": "create_task", "arguments": { "title": "appeler Paul", "datetime": "2025-04-16T10:00:00" } } 
↓ Ton backend ajoute la tâche dans ta todo list. 
↓ Le LLM rédige : "C'est noté ! J'ai ajouté un rappel pour appeler Paul demain à 10h."

[Diagramme Mermaid](./flow.mermaid)

---

## 🛠️ Fonctions disponibles (JSON Schema)

Ces fonctions sont transmises au modèle via l’API, au format JSON Schema :

### 📝 1. `create_task`

Crée une nouvelle tâche.

```json
{
  "name": "create_task",
  "description": "Ajoute une nouvelle tâche à la todo list",
  "parameters": {
    "type": "object",
    "properties": {
      "title": {
        "type": "string",
        "description": "Le titre ou sujet de la tâche"
      },
      "datetime": {
        "type": "string",
        "description": "Date et heure au format ISO 8601 (ex: 2025-04-16T10:00:00)"
      }
    },
    "required": ["title", "datetime"]
  }
}
```


### 📝 2. `list_tasks`

Retourne la liste des tâches.

```json
{
  "name": "list_tasks",
  "description": "Retourne la liste actuelle des tâches",
  "parameters": {
    "type": "object",
    "properties": {}
  }
}
```

### 📝 3. `delete_task`

Retourne la liste des tâches.

```json
{
  "name": "delete_task",
  "description": "Supprime une tâche à partir de son titre",
  "parameters": {
    "type": "object",
    "properties": {
      "title": {
        "type": "string",
        "description": "Le titre de la tâche à supprimer"
      }
    },
    "required": ["title"]
  }
}
```
