# 🤖 function-calling-assistant avec Spring AI

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

En quelques lignes, le `Function Calling`, c’est une manière de demander à un LLM (modèle de langage) comme GPT-4, d'appeler automatiquement des fonctions de ton code en fonction de ce que l’utilisateur demande.

Tu lui dis : « Voici les fonctions que tu peux utiliser, voici ce qu’elles font ».
Ensuite, GPT analyse la requête de l’utilisateur, et choisit tout seul la fonction à appeler (avec les bons arguments).

🧠 Tu n’as plus à parser manuellement les requêtes utilisateurs → c’est le LLM qui comprend et structure les appels.
Ça permet de connecter une IA à des actions concrètes dans ton app, comme interroger une BDD, envoyer un email ou lancer une recherche.

### Exemple :

**Utilisateur : "Quelle météo fait-il à Barcelone"**

↓ Modèle : { "name": "recuperer_meteo", "arguments": { "ville": "Barcelone" } }  
↓ Ton backend contact le service permettant de récupérer la météo de Barcelone aujourd’hui. 
↓ Le LLM rédige : "Il fait 25°C aujourd’hui dans Barcelone, avec un vent de 10 km/h et des nuages."

---

## 🛠️ Fonctions disponibles (JSON Schema)

Ces fonctions sont transmises au modèle via l’API, au format JSON Schema :

### 📝 1. `recuperer_meteo`

Récupérer la météo.

```json
{
  "name": "recuperer_meteo",
  "description": "Retourne la météo de la ville indiquée",
  "parameters": {
    "type": "object",
    "properties": {
      "ville": {
        "type": "string",
        "description": "Le nom de la ville"
      }
    }
  }
}
```

### Configuration

Ajouter les variables d’environnement suivantes : `OPENAI_API_KEY` & `OPEN_WEATHER_API_KEY`.