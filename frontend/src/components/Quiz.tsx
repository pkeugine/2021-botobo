import { css } from '@emotion/react';
import styled from '@emotion/styled';
import React, { useEffect, useState } from 'react';
import { useRecoilValue } from 'recoil';

import { QUIZ_MODE } from '../constants';
import { quizModeState } from '../recoil';
import { Flex, scrollBarStyle } from '../styles';
import { QuizResponse } from '../types';

interface Props extends Omit<QuizResponse, 'id'> {
  isChanged: boolean;
}

interface CardStyleProps {
  isFlipped: boolean;
}

const Quiz = ({
  question,
  answer,
  workbookName,
  encounterCount,
  isChanged,
}: Props) => {
  const quizMode = useRecoilValue(quizModeState);
  const [isFlipped, setIsFlipped] = useState(false);

  useEffect(() => {
    setIsFlipped(false);
  }, [isChanged]);

  return (
    <Container onClick={() => setIsFlipped((prevValue) => !prevValue)}>
      <Card isFlipped={isFlipped}>
        <Question>
          <TopContent>
            <WorkbookName>{workbookName}</WorkbookName>
            {quizMode === QUIZ_MODE.DEFAULT && (
              <EncounterCount>풀어본 횟수: {encounterCount}</EncounterCount>
            )}
          </TopContent>
          <Text>
            <span>Q. {question}</span>
          </Text>
        </Question>
        <Answer>
          <TopContent>
            <WorkbookName>{workbookName}</WorkbookName>
          </TopContent>
          <Text>
            <span>A. {answer}</span>
          </Text>
        </Answer>
      </Card>
    </Container>
  );
};

const Container = styled.div`
  height: 12.5rem;
  width: 100%;
  perspective: 50rem;
`;

const Card = styled.div<CardStyleProps>`
  height: 100%;
  width: 100%;
  transition: transform 0.5s ease;
  transform-style: preserve-3d;
  cursor: pointer;
  position: relative;

  ${({ isFlipped }) =>
    isFlipped &&
    css`
      transform: rotateX(180deg);
    `}

  & > div {
    position: absolute;
    padding: 1rem;
    padding-top: 2.5rem;
    width: 100%;
    height: 100%;
    backface-visibility: hidden;

    ${({ theme }) =>
      css`
        box-shadow: ${theme.boxShadow.card};
        border-radius: ${theme.borderRadius.square};
      `}
  }
`;

const TopContent = styled.div`
  ${Flex({ justify: 'space-between', items: 'center' })};
  position: absolute;
  width: 100%;
  top: 1rem;
  left: 0;
  padding: 0 1rem;
  backface-visibility: hidden;
`;

const WorkbookName = styled.span`
  ${({ theme }) => css`
    color: ${theme.color.pink};
    font-weight: ${theme.fontWeight.bold};
  `}
`;

const EncounterCount = styled.span`
  ${({ theme }) => css`
    color: ${theme.color.gray_6};
    font-size: ${theme.fontSize.small};
  `}
`;

const Question = styled.div`
  transform: rotateX(0deg);
  overflow-y: auto;

  ${({ theme }) => css`
    background-color: ${theme.color.white};
  `}
`;

const Answer = styled.div`
  transform: rotateX(180deg);
  overflow-y: auto;

  ${({ theme }) => css`
    background-color: ${theme.color.white};
  `}
`;

const Text = styled.div`
  overflow-y: auto;
  height: 9rem;
  line-height: 9rem;

  ${scrollBarStyle};

  & > span {
    display: inline-block;
    vertical-align: middle;
    line-height: 1.4rem;
  }
`;

export default Quiz;
