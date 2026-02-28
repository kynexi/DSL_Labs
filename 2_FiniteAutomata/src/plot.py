import json
from pyvis.network import Network

def plot_dfa_from_json(filename):
    with open(filename) as f:
        data = json.load(f)

    transitions = data["transitions"]
    final_states = set(data["final_states"])

    graph = Network(height="600px", width="100%", notebook=True, directed=True)

    for state, trans in transitions.items():
        shape = "doublecircle" if state in final_states else "circle"
        graph.add_node(state, label=state, shape=shape)

        for symbol, next_states in trans.items():
            for next_state in next_states:
                shape_next = "doublecircle" if next_state in final_states else "circle"
                graph.add_node(next_state, label=next_state, shape=shape_next)
                graph.add_edge(state, next_state, label=symbol)

    graph.show("dfa_graph.html")

if __name__ == "__main__":
    plot_dfa_from_json("out.json")