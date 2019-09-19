import stanfordnlp
from flask import Flask, request, jsonify
from flask_restful import reqparse, Resource, Api
import json
from flask_cors import CORS

from py_snlp import parse_snlp as psnlp

app = Flask(__name__)
CORS(app)
api = Api(app)

Pipeline = stanfordnlp.Pipeline(lang='ja')

parser = reqparse.RequestParser()
parser.add_argument('app', type=str)
parser.add_argument('text', type=str)


class ParseSnlp(Resource):
    def get(self):
        pass

    def post(self):
        data = parser.parse_args()
        print("hogehoge", data)
        text = data['text']
        if data['app'] == 'parse-snlp' and len(text) >= 1:
            analyzed = psnlp.refinement_doc(psnlp.get_doc(Pipeline, text))
            res = {
                'original text': text,
                'type': 'dependencies',
                'result_sentence': analyzed.sentences,
                'result_tree': analyzed.tree
            }
            return jsonify(res)
        else:
            pass


api.add_resource(ParseSnlp, '/parse-snlp/dependencies')

if __name__ == '__main__':
    app.run(host='localhost', port=8810, debug=True)
