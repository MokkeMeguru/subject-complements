import stanfordnlp

LEVEL = ["DEBUG", "INFO", "WARN", "ERROR"]
SAMPLE_TEXT = "山田さんは、明日の夕食はカレーにしようと言った。"


def gen_info(level: int, message: str):
    return "[{}]: {}".format(LEVEL[level], message)


def test_stanfordnlp(text: str):
    """test stanfordnlp using a  japanese sentence.
    Parameters:
    --------
    text: string
    a sentence written by japanese

    Returns:
    ----------
    None
    """

    def get_doc(text: str):
        Pipeline = stanfordnlp.Pipeline(lang='ja')
        doc = Pipeline(text)
        return doc

    doc = get_doc(text)
    print(gen_info(1, "the input sentence is {}".format(doc.text)))
    assert type(doc.text) == str, gen_info(3,
                                           "unexpected type(Pipeline's text)")
    print(gen_info(1, "print dependencies"))
    print(doc.sentences[0].print_dependencies())


def main():
    print(gen_info(1, "download resources analyse japanese text"))
    print(gen_info(1, "please answer Y for installation these resources"))
    stanfordnlp.download('ja')
    print(gen_info(1, "finish downloading"))
    print(gen_info(1, "testing dependency modules"))
    test_stanfordnlp(SAMPLE_TEXT)
    print(gen_info(1, "finish testing"))


if __name__ == '__main__':
    main()
