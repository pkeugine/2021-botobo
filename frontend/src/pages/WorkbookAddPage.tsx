import { css } from '@emotion/react';
import styled from '@emotion/styled';
import React, { useState } from 'react';

import {
  Hashtag,
  InputField,
  MainHeader,
  PageHeader,
  Toggle,
} from '../components';
import { ROUTE, WORKBOOK_NAME_MAXIMUM_LENGTH } from '../constants';
import { FormProvider } from '../contexts';
import { useWorkbook } from '../hooks';
import { Flex } from '../styles';
import { TagResponse } from '../types';

const validateWorkbookName = (value: string) => {
  if (value.length > WORKBOOK_NAME_MAXIMUM_LENGTH) {
    throw new Error(
      `문제집 이름은 ${WORKBOOK_NAME_MAXIMUM_LENGTH}자 이하여야 합니다.`
    );
  }
};

const WorkbookAddPage = () => {
  const [hashtags, setHashtags] = useState<TagResponse[]>([]);
  const [isPublic, setIsPublic] = useState(true);
  const { createWorkbook } = useWorkbook();

  return (
    <>
      <MainHeader sticky={false} shadow={false} />
      <FormProvider
        initialValues={{ name: '' }}
        validators={{ name: validateWorkbookName }}
        onSubmit={({ name }) => createWorkbook(name, hashtags, isPublic)}
      >
        <PageHeader
          title={ROUTE.WORKBOOK_ADD.TITLE}
          rightContent={<button>확인</button>}
        />
        <Container>
          <ToggleWrapper>
            <Toggle
              labelText={'전체 공개'}
              isChecked={isPublic}
              onChange={({ target }) => setIsPublic(target.checked)}
            />
          </ToggleWrapper>
          <Input
            name="name"
            placeholder="문제집 이름"
            focusColor="gray"
            autoFocus={true}
            maxLength={WORKBOOK_NAME_MAXIMUM_LENGTH}
          />
          <Hashtag hashtags={hashtags} setHashtags={setHashtags} />
        </Container>
      </FormProvider>
    </>
  );
};

const Container = styled.div`
  ${({ theme }) =>
    css`
      padding: ${theme.pageSize.padding};
    `}
`;

const ToggleWrapper = styled.div`
  ${Flex({ justify: 'flex-end', items: 'center' })};
  margin-bottom: 1rem;
  width: 100%;

  & > span {
    margin-right: 1rem;
  }
`;

const Input = styled(InputField)`
  width: 100%;
  padding: 0.5rem 1rem;
  transition: border 0.2s linear;
  outline: none;
  border: none;
  background-color: transparent;

  ${({ theme }) => css`
    font-size: ${theme.fontSize.default};
    border-bottom: 1px solid ${theme.color.gray_5};
  `}
`;

export default WorkbookAddPage;
